package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

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
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
            
    @ApiModelProperty("支付宝账号")
    private String identity;
            
    @ApiModelProperty("本人手机号")
    private String phoneNum;
            
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("头像")
    private String avatar;
            
    @ApiModelProperty("驾驶员姓名")
    private String driverName;
            
    @ApiModelProperty("性别")
    private Integer sex;
            
    @ApiModelProperty("身份证")
    private String idCard;
            
    @ApiModelProperty("驾驶证图片")
    private String driverLicense;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty(value = "是否被锁定")
    private Boolean locked;

    @ApiModelProperty(value = "是否被启用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建日期", example = "2020-02-05 13:30:41")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期", example = "2020-02-05 13:30:41")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

