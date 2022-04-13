package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.pzhu.substitute.common.status.OrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dengyiqing
 * @description 订单日志(PzhuOrderLog)表实体类
 * @date 2022-03-19 15:01:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("订单日志")
public class OrderLog extends Model<OrderLog> implements Serializable {
    private static final long serialVersionUID = -98329829132696335L;
            
    @ApiModelProperty("ID")
    private Long id;
            
    @ApiModelProperty("订单编号")
    private Long orderId;
            
    @ApiModelProperty("订单状态")
    private OrderStatus orderStatus;

    @ApiModelProperty("发生时间")
    @TableField(value = "operate_date", fill = FieldFill.INSERT)
    private Date operateDate;
}

