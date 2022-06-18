package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("司机余额")
@AllArgsConstructor
@NoArgsConstructor
public class DriverCertify extends Model<DriverCertify> implements Serializable {
    private static final long serialVersionUID = 4695357379447277910L;

    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("驾驶员ID")
    private Long driverId;

    @ApiModelProperty("驾驶员名称")
    private String name;

    @ApiModelProperty("是否完成")
    private Boolean complete;

    @ApiModelProperty("驾驶证地址")
    private String driverLicense;

    @ApiModelProperty("性别")
    private Integer sex;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty(value = "创建日期", example = "2020-02-05 13:30:41")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新日期", example = "2020-02-05 13:30:41")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
