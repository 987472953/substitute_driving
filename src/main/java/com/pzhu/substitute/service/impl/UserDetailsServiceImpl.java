package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.entity.DriverInfo;
import com.pzhu.substitute.entity.LoginDriver;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.mapper.DriverMapper;
import com.pzhu.substitute.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dengyiqing
 * @description spring security 用户管理service
 * @date 2022/1/12
 */
@Slf4j
@Service
public class UserDetailsServiceImpl<T> implements UserDetailsService {

    private final UserMapper userMapper;
    private final DriverMapper driverMapper;

    @Autowired
    public UserDetailsServiceImpl(UserMapper userMapper, DriverMapper driverMapper) {
        this.userMapper = userMapper;
        this.driverMapper = driverMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("获得用户信息");
        String[] split = username.split(CommonConstants.SPLIT_REGEX);
        if (split.length > 1) {
            username = split[1];
        }
        if (CommonConstants.DRIVER_ROLE.equals(split[0])) {
            LambdaQueryWrapper<DriverInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DriverInfo::getPhoneNum, username);
            DriverInfo driverInfo = driverMapper.selectOne(wrapper);
            List<String> permissions = driverMapper.queryPermissionsByDriverId(driverInfo.getId());

            return new LoginDriver(driverInfo, permissions);
        } else {
            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getPhoneNum, username);
            UserInfo userInfo = userMapper.selectOne(wrapper);
            log.debug("获得用户权限信息");
            List<String> permissions = userMapper.queryPermissionsByUserId(userInfo.getId());
            return new LoginUser(userInfo, permissions);
        }
    }
}