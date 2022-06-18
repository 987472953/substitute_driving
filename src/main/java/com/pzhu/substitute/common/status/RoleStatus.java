package com.pzhu.substitute.common.status;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author dengyiqing
 * @description 订单状态
 * @date 2022/4/27
 */
public enum RoleStatus implements IEnum<Integer> {
    USER(0, "user"), DRIVER(1, "driver");

    private Integer roleId;
    private String roleName;

    RoleStatus(Integer roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public Integer getValue() {
        return roleId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
