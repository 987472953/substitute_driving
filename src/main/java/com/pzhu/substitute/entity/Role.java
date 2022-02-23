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
 * @description 角色(Role)表实体类
 * @date 2022-01-15 16:19:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("角色")
public class Role extends Model<Role> implements Serializable {
    private static final long serialVersionUID = 821564392341121240L;
            
    @ApiModelProperty("ID")
    private Object id;
            
    @ApiModelProperty("角色名称")
    private String roleName;
            
    @ApiModelProperty("角色英文关键字")
    private String roleKey;
            
    @ApiModelProperty("角色状态(1正常0停用)")
    private Integer status;
            
    @ApiModelProperty("创建人id")
    private Integer createBy;
            
    @ApiModelProperty("创建时间")
    private Date createTime;
            
    @ApiModelProperty("更新人id")
    private Integer updateBy;
            
    @ApiModelProperty("更新时间")
    private Date updateTime;
            
    @ApiModelProperty("备注")
    private String remark;
}

