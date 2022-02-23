package com.pzhu.substitute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pzhu.substitute.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dengyiqing
 * @description 角色(Role)表数据库访问层
 * @date 2022-01-15 16:19:46
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}

