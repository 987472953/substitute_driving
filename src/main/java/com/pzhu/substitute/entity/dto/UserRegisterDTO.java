package com.pzhu.substitute.entity.dto;

import lombok.Data;

/**
 * @author dengyiqing
 * @description 用户注册
 * @date 2022/4/10
 */
@Data
public class UserRegisterDTO {
    private String phoneNum;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String avatar;
    private Integer sex;
    private String verify;
}
