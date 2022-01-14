package com.pzhu.substitute.service;

import com.pzhu.substitute.common.Result;

/**
 * @author dengyiqing
 * @description 用户管理service
 * @date 2022/1/14
 */
public interface UserService {

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    Result login(String username, String password);
}
