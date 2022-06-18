package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("评论表")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefund extends Model<OrderLog> implements Serializable {
    private static final long serialVersionUID = -7929579252926566192L;
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单编号")
    private Long orderId;

    @ApiModelProperty("支付宝交易编号")
    private String tradeNo;

    @ApiModelProperty("实际退款金额")
    private Double refundFee;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}
