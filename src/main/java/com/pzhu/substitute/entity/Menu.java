package com.pzhu.substitute.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dengyiqing
 * @description 权限表(Menu)表实体类
 * @date 2022-01-15 16:19:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("权限表")
public class Menu extends Model<Menu> implements Serializable {
    private static final long serialVersionUID = -89039416845043067L;

    @TableId
    @ApiModelProperty("ID")
    private Integer id;
            
    @ApiModelProperty("权限名称")
    private String menuName;
            
    @ApiModelProperty("权限关键字")
    private String permKey;
            
    @ApiModelProperty("路由地址")
    private String path;
            
    @ApiModelProperty("组件路径")
    private String component;
            
    @ApiModelProperty("菜单状态(1正常0停用)")
    private Integer status;
            
    @ApiModelProperty("菜单图标")
    private String icon;
            
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

