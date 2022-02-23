package com.pzhu.substitute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pzhu.substitute.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dengyiqing
 * @description 订单表(Order)表数据库访问层
 * @date 2022-02-10 18:03:40
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}

