package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.pzhu.substitute.common.status.OrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dengyiqing
 * @description 订单表(Order)表实体类
 * @date 2022-02-10 18:03:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("订单表")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order extends Model<Order> implements Serializable {
    private static final long serialVersionUID = -53314457093416446L;

    @ApiModelProperty("ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("订单手机号")
    private String orderPhone;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("司机id")
    private Long driverId;

    @ApiModelProperty("订单状态")
    private OrderStatus orderStatus;

    @ApiModelProperty("订单号")
    private String tradeNo;

    @ApiModelProperty("起点")
    private String startPoint;

    @ApiModelProperty("终点")
    private String endPoint;

    @ApiModelProperty("订单距离")
    private Integer distance;

    @ApiModelProperty("起点地址")
    private String startAddress;

    @ApiModelProperty("终点地址")
    private String endAddress;

    @ApiModelProperty("支付类型")
    private Integer paymentType;

    @ApiModelProperty("预计价格")
    private Double paymentAmount;

    @ApiModelProperty("实际支付金额")
    private Double actualAmount;

    @ApiModelProperty("是否为预约单")
    private Boolean isReservation;

    @ApiModelProperty("预约时间")
    private Date appointmentTime;

    @ApiModelProperty("备注")
    private String comment;

    @ApiModelProperty("下订单时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("订单修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

