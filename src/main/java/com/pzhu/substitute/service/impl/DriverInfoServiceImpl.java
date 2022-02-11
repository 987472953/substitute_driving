package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.substitute.entity.DriverInfo;
import com.pzhu.substitute.mapper.DriverInfoMapper;
import com.pzhu.substitute.service.DriverInfoService;
import org.springframework.stereotype.Service;

/**
 * @author dengyiqing
 * @description (DriverInfo)表服务实现类
 * @date 2022-01-15 01:45:37
 */
@Service("driverInfoService")
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {

}

