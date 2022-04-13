package com.pzhu.substitute.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dengyiqing
 * @description 登录dto
 * @date 2022/3/26
 */
@Data
public class LoginDTO {
    @ApiModelProperty("手机号")
    private String phoneNum;

    @ApiModelProperty("登录密码")
    private String password;

    private String uuid;
    private String code;
    private String loginRule;
}
