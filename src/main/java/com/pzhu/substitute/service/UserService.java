package com.pzhu.substitute.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.UserInfo;

/**
 * @author dengyiqing
 * @description 用户管理service
 * @date 2022/1/14
 */
public interface UserService extends IService<UserInfo> {

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    Result login(String username, String password);

    /**
     * 退出登录
     * @return
     */
    Result logout();
}
