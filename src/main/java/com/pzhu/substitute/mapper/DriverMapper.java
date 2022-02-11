package com.pzhu.substitute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pzhu.substitute.entity.DriverInfo;

import java.util.List;

/**
 * @author dengyiqing
 * @description 代驾员管理dao
 * @date 2022/1/18
 */
public interface DriverMapper extends BaseMapper<DriverInfo> {
    List<String> queryPermissionsByDriverId(Long driverId);
}
