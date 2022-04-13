package com.pzhu.substitute.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.DriverInfo;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;

/**
 * @author dengyiqing
 * @description (DriverInfo)表服务接口
 * @date 2022-01-15 01:45:37
 */
public interface DriverService extends IService<DriverInfo> {

    Result register(UserRegisterDTO userRegisterDTO);
}
