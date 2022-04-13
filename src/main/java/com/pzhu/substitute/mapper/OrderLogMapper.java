package com.pzhu.substitute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pzhu.substitute.entity.OrderLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dengyiqing
 * @description 订单日志(PzhuOrderLog)表数据库访问层
 * @date 2022-03-19 15:01:50
 */
@Mapper
public interface OrderLogMapper extends BaseMapper<OrderLog> {

}

