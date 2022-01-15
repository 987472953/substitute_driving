package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dengyiqing
 * @description spring security 用户管理service
 * @date 2022/1/12
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("获得用户信息");
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhoneNum, username);
        UserInfo userInfo = userMapper.selectOne(wrapper);
        log.debug("获得用户权限信息");
        List<String> permissions = userMapper.queryPermissionsByUserId(userInfo.getId());
        return new LoginUser(userInfo, permissions);
    }
}
