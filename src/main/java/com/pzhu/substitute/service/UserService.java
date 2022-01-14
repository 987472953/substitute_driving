package com.pzhu.substitute.service;

import com.pzhu.substitute.common.Result;

/**
 * @author dengyiqing
 * @description TODO
 * @date 2022/1/14
 */
public interface UserService {

    /**
     *
     * @param username
     * @param password
     * @return
     */
    Result login(String username, String password);
}
