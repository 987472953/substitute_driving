package com.pzhu.substitute.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author dengyiqing
 * @description 驾驶员认证
 * @date 2022/4/14
 */
@Data
public class DriverCertifyDTO {
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("性别")
    private Integer sex;
    @ApiModelProperty("头像地址")
    private String avatar;
    @ApiModelProperty("准驾车型")
    private String drivingType;
    @ApiModelProperty("驾驶证地址")
    private String driverLicense;
    @ApiModelProperty("国籍")
    private String nationality;
    @ApiModelProperty("身份证号")
    private String idCard;
    @ApiModelProperty("住址")
    private String address;
    @ApiModelProperty("出生日期")
    private Date birthDate;
    @ApiModelProperty("初次领证日期")
    private Date initialReceiptDate;
    @ApiModelProperty("证件有效期限")
    private Date effectiveStartDate;
    @ApiModelProperty("证件有效期限")
    private Date effectiveEndDate;
}
