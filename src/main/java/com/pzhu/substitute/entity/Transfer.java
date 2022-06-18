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
@ApiModel("角色")
@AllArgsConstructor
@NoArgsConstructor
public class Transfer extends Model<Transfer> implements Serializable {

    private static final long serialVersionUID = -8021907859590596414L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("转账单ID")
    private Long id;

    @ApiModelProperty("支付宝转账订单")
    private String transferOrderId;

    @ApiModelProperty("代驾员ID")
    private Long driverId;

    @ApiModelProperty("转账金额")
    private Double transAmount;

    @ApiModelProperty("支付宝账号类型")
    private String identityType;

    @ApiModelProperty("支付宝账号")
    private String identity;

    @ApiModelProperty(value = "创建日期", example = "2020-02-05 13:30:41")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}
