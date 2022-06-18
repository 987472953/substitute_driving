package com.pzhu.substitute.service.impl;

import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.entity.DriverInfo;
import com.pzhu.substitute.entity.LoginDriver;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.entity.dto.LoginDTO;
import com.pzhu.substitute.service.CommonService;
import com.pzhu.substitute.utils.JwtUtil;
import com.pzhu.substitute.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * @author dengyiqing
 * @description 一般接口的实现
 * @date 2022/4/11
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    private final RedisUtil redisUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public CommonServiceImpl(RedisUtil redisUtil, AuthenticationManager authenticationManager) {
        this.redisUtil = redisUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Result createCode(String phoneNum) {
        Random random = new Random();
        String code = random.nextInt(1000000) + "";
        // TODO 发送短信验证码
        // TODO 再次发送时为更新值value, 需要一定的幂等
        redisUtil.set(CommonConstants.REGISTER_CODE + phoneNum, code, 5 * 60);
        return Result.ok(ResultCode.CREATED);
    }

    @Override
    public void checkLoginCode(String uuid, String code) {
        if (code == null || uuid == null) {
            throw new BizException(ResultCode.CODE_ERROR).setErrorMsg("验证码为空");
        }
        String key = CommonConstants.LOGIN_IMAGE_CODE_PREFIX + uuid + CommonConstants.LOGIN_SUFFIX;
        String redisCode = (String) redisUtil.get(key);
        if (!code.equals(redisCode)) {
            throw new BizException(ResultCode.CODE_ERROR).setErrorMsg("验证码错误");
        }
    }

    @Override
    public Result login(LoginDTO loginDTO) {
        String username = loginDTO.getLoginRule() + ":" + loginDTO.getPhoneNum();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, loginDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            log.debug("[登录]({})密码验证错误", username);
            throw new BizException(ResultCode.LOGIN_FAIL);
        }
        Object principal = authenticate.getPrincipal();
        Long id;
        String role;
        String redisKey;
        if (principal instanceof LoginUser) {
            UserInfo userInfo = ((LoginUser) principal).getUserInfo();
            id = userInfo.getId();
            userInfo.setPassword(null);
            role = CommonConstants.USER_ROLE;
            redisKey = CommonConstants.USER_PREFIX + userInfo.getId() + CommonConstants.LOGIN_SUFFIX;
        } else {
            DriverInfo driverInfo = ((LoginDriver) principal).getDriverInfo();
            id = driverInfo.getId();
            driverInfo.setPassword(null);
            role = CommonConstants.DRIVER_ROLE;
            redisKey = CommonConstants.DRIVER_PREFIX + driverInfo.getId() + CommonConstants.LOGIN_SUFFIX;
        }
        String jwt = JwtUtil.createJWT(CommonConstants.JWT_SALT, 2 * 60 * 60 * 1000, id, role);
        log.debug("[用户登录]({})登录成功", id);

        HashMap<Object, Object> map = new HashMap<>();
        map.put("token", jwt);

        redisUtil.set(redisKey, principal);

        return Result.ok(map, "登录成功");
    }
}
