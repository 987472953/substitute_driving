package com.pzhu.substitute.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author dengyiqing
 * @description 修改用户信息
 * @date 2022/2/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO implements Serializable {
    private static final long serialVersionUID = 33332;
    private Long id;
    private String nickname;
    private String avatar;
    private Integer sex;
    private Integer age;
}
