package com.pzhu.substitute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pzhu.substitute.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dengyiqing
 * @description 权限表(Menu)表数据库访问层
 * @date 2022-01-15 16:19:46
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

}

