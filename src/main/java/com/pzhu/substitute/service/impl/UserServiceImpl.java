package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.ResultCode;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.mapper.UserMapper;
import com.pzhu.substitute.service.UserService;
import com.pzhu.substitute.utils.JwtUtil;
import com.pzhu.substitute.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * @author dengyiqing
 * @description 用户管理service实现
 * @date 2022/1/14
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserInfo> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Result login(String username, String password) {

        username = CommonConstants.USER_ROLE + ":" + username;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new BizException(ResultCode.LOGIN_FAIL);
        }
        LoginUser principal = (LoginUser) authenticate.getPrincipal();
        UserInfo userInfo = principal.getUserInfo();
        // TODO 修改过期时间
        String jwt = JwtUtil.createJWT(CommonConstants.JWT_SALT, 24 * 60 * 60 * 1000, userInfo.getPhoneNum());
        log.info("用户[{}]登录成功", userInfo.getPhoneNum());

        HashMap<String, String> map = new HashMap<>();
        map.put("token", jwt);

        userInfo.setPassword(null);
        String redisKey = CommonConstants.USER_PREFIX + userInfo.getPhoneNum() + CommonConstants.LOGIN_SUFFIX;
        redisUtil.set(redisKey, principal);

        return Result.ok(map, "登录成功");
    }

    @Override
    public Result logout() {
        log.debug("[退出登录]获得SecurityContext中的用户Id");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String phoneNum = loginUser.getUserInfo().getPhoneNum();
        String redisKey = CommonConstants.USER_PREFIX + phoneNum + CommonConstants.LOGIN_SUFFIX;
        redisUtil.del(redisKey);
        return Result.ok().message("成功退出登录");
    }

    @Override
    public Result register(String username, String password, String code) {

        String redis_code = (String) redisUtil.get(CommonConstants.REGISTER_CODE + username);
        if (Objects.isNull(code) || !code.equals(redis_code)) {
            throw new BizException(ResultCode.CODE_INCORRECT);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum(username);
        userInfo.setPassword(passwordEncoder.encode(password));
        int insert = userMapper.insert(userInfo);
        return insert == 1 ? new Result(ResultCode.CREATED) : new Result(ResultCode.FAIL);
    }

    @Override
    public Result createCode(String phoneNum) {
        Random random = new Random();
        String code = random.nextInt(1000000) + "";
        // TODO 发送短信验证码
        // TODO 再次发送时为更新值value, 需要一定的幂等
        redisUtil.set(CommonConstants.REGISTER_CODE + phoneNum, code, 60);
        return Result.ok(ResultCode.CREATED);
    }

}
