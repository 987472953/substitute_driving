package com.pzhu.substitute.service;

import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.dto.LoginDTO;

/**
 * @author dengyiqing
 * @description 通用的service
 * @date 2022/4/11
 */
public interface CommonService {
    /**
     * 发送验证码
     * @param phoneNum 手机号
     * @return
     */
    Result createCode(String phoneNum);

    /**
     * 检查验证码
     * @param uuid 之前的随机数
     * @param code 输入的验证码
     * @return
     */
    void checkLoginCode(String uuid, String code);

    Result login(LoginDTO loginDTO);
}
