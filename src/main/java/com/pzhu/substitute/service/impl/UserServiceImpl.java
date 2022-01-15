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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Objects;

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

    @Value("${jwt.salt}")
    private String jwtSalt;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public Result login(String username, String password) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new BizException(ResultCode.LOGIN_FAIL);
        }
        LoginUser principal = (LoginUser) authenticate.getPrincipal();
        UserInfo userInfo = principal.getUserInfo();
        String jwt = JwtUtil.createJWT(jwtSalt, -1, userInfo.getPhoneNum());
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
        String redisKey = CommonConstants.USER_PREFIX + loginUser.getUserInfo().getPhoneNum() + CommonConstants.LOGIN_SUFFIX;
        redisUtil.del(redisKey);
        return Result.ok().message("成功退出登录");
    }
}
