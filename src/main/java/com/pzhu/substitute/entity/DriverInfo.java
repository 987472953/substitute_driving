package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dengyiqing
 * @description (DriverInfo)表实体类
 * @date 2022-01-15 01:45:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("司机信息")
public class DriverInfo extends Model<DriverInfo> implements Serializable {
    private static final long serialVersionUID = 589295549590144920L;
            
    @ApiModelProperty(" ID")
    private Long id;
            
    @ApiModelProperty("微信登录标识")
    private Integer openid;
            
    @ApiModelProperty("本人手机号")
    private String phoneNum;
            
    @ApiModelProperty("密码")
    private String password;
            
    @ApiModelProperty("驾驶员姓名")
    private String driverName;
            
    @ApiModelProperty("性别")
    private Integer sex;
            
    @ApiModelProperty("身份证")
    private String idCard;
            
    @ApiModelProperty("驾驶证")
    private String driverLicense;
            
    @ApiModelProperty("创建时间")
    private Date createTime;
            
    @ApiModelProperty("更新时间")
    private Date updateTime;
}

