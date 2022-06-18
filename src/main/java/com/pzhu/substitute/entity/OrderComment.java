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


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("评论表")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderComment extends Model<OrderComment> implements Serializable {

    private static final long serialVersionUID = -5486662563645399776L;

    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单编号")
    private Long orderId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("评论")
    private String comment;

    @ApiModelProperty("角色编号 0 用户, 1 司机")
    private RoleStatus roleStatus;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

}
