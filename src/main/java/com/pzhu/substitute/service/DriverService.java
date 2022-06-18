package com.pzhu.substitute.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.DriverBalance;
import com.pzhu.substitute.entity.DriverInfo;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.dto.DriverCertifyDTO;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;

import java.util.List;

/**
 * @author dengyiqing
 * @description (DriverInfo)表服务接口
 * @date 2022-01-15 01:45:37
 */
public interface DriverService extends IService<DriverInfo> {

    Result register(UserRegisterDTO userRegisterDTO);

    /**
     * 验证驾驶证
     * @param driverCertifyDTO 驾驶证信息
     * @return
     */
    Result certify(DriverCertifyDTO driverCertifyDTO, DriverInfo driverInfo);

    boolean canTakeOrder(Long id);

    Result completeTheOrder(Long orderId, DriverInfo driverInfo);

    List<Order> getMyOrder(DriverInfo driverInfo);

    DriverBalance queryBalance(DriverInfo driverInfo);
}
