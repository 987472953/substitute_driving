package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.pzhu.substitute.common.status.RoleStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dengyiqing
 * @description 消息(Message)表实体类
 * @date 2022-02-10 18:03:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("消息表")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message extends Model<Message> implements Serializable {
    private static final long serialVersionUID = -5041109969910526613L;
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("角色编号 0 用户, 1 司机")
    private RoleStatus roleStatus;

    @ApiModelProperty("订单状态")
    private String msg;

    @ApiModelProperty("发生时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}
