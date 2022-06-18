package com.pzhu.substitute.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.DriverCertify;
import com.pzhu.substitute.entity.DriverInfo;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.Transfer;
import com.pzhu.substitute.entity.dto.LoginDTO;

import java.util.List;

public interface AdminService {
    /**
     * 管理员登录
     * @param loginDTO
     */
    Result login(LoginDTO loginDTO);

    /**
     * 查询所有的司机
     */
    Page<DriverInfo> queryPageDrivers(int page, int size);

    Page<Order> queryPageOrders(int page, int size);

    Page<Transfer> queryPageTransfer(Integer page, Integer size);

    List<DriverCertify> queryDriverCertify(String driverId);

    DriverCertify driverCertifySuccess(Integer id);

    DriverCertify driverCertifyFail(Integer id);
}
