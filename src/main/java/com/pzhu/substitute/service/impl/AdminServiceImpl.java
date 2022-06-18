package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.entity.*;
import com.pzhu.substitute.entity.dto.LoginDTO;
import com.pzhu.substitute.mapper.*;
import com.pzhu.substitute.service.AdminService;
import com.pzhu.substitute.utils.JwtUtil;
import com.pzhu.substitute.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final RoleMapper roleMapper;
    private final DriverMapper driverMapper;
    private final OrderMapper orderMapper;
    private final TransferMapper transferMapper;
    private final DriverCertifyMapper driverCertifyMapper;
    private final RedisUtil redisUtil;

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, RoleMapper roleMapper, DriverMapper driverMapper, OrderMapper orderMapper, TransferMapper transferMapper, DriverCertifyMapper driverCertifyMapper, RedisUtil redisUtil) {
        this.adminMapper = adminMapper;
        this.roleMapper = roleMapper;
        this.driverMapper = driverMapper;
        this.orderMapper = orderMapper;
        this.transferMapper = transferMapper;
        this.driverCertifyMapper = driverCertifyMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public Result login(LoginDTO loginDTO) {

        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, loginDTO.getUsername()).eq(Admin::getPassword, loginDTO.getPassword());
        Admin admin = adminMapper.selectOne(wrapper);
        if (Objects.isNull(admin)) {
            throw new BizException(ResultCode.LOGIN_FAIL).setErrorMsg("用户不存在");
        }
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getId, admin.getRoleId());
        Role role = roleMapper.selectOne(roleLambdaQueryWrapper);
        String jwt = JwtUtil.createJWT(CommonConstants.JWT_SALT, 2 * 60 * 60 * 1000, admin.getId().longValue(), role.getRoleKey());
        return Result.ok().data("token", jwt);
    }

    @Override
    public Page<DriverInfo> queryPageDrivers(int page, int size) {
        Page<DriverInfo> driverInfoPage = new Page<>(page, size);
        driverMapper.selectPage(driverInfoPage, null);
        driverInfoPage.getRecords().forEach(driverInfo -> driverInfo.setPassword(""));
        return driverInfoPage;
    }

    @Override
    public Page<Order> queryPageOrders(int page, int size) {
        Page<Order> orderPage = new Page<>(page, size);
        orderMapper.selectPage(orderPage, null);
        return orderPage;
    }

    @Override
    public Page<Transfer> queryPageTransfer(Integer page, Integer size) {
        Page<Transfer> transferPage = new Page<>(page, size);
        transferMapper.selectPage(transferPage, null);
        return transferPage;
    }

    @Override
    public List<DriverCertify> queryDriverCertify(String driverId) {
        LambdaQueryWrapper<DriverCertify> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DriverCertify::getDriverId, driverId);
        return driverCertifyMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public DriverCertify driverCertifySuccess(Integer id) {
        DriverCertify driverCertify = driverCertifyMapper.selectById(id);
        driverCertify.setComplete(true);
        driverCertifyMapper.updateById(driverCertify);
        DriverInfo driverInfo = driverMapper.selectById(driverCertify.getDriverId());
        driverInfo.setDriverName(driverCertify.getName());
        driverInfo.setIdCard(driverCertify.getIdCard());
        driverInfo.setDriverLicense(driverCertify.getDriverLicense());
        driverInfo.setAvatar(driverCertify.getAvatar());
        driverInfo.setSex(driverCertify.getSex());
        driverMapper.updateById(driverInfo);
        String driverKey = CommonConstants.DRIVER_PREFIX + driverInfo.getId() + CommonConstants.LOGIN_SUFFIX;
        Object o = redisUtil.get(driverKey);
        if(Objects.nonNull(o)){
            ((LoginDriver) o).setDriverInfo(driverInfo);
            redisUtil.set(driverKey, o);
        }
        return driverCertify;
    }

    @Override
    public DriverCertify driverCertifyFail(Integer id) {
        DriverCertify driverCertify = new DriverCertify();
        driverCertify.setId(id);
        driverCertify.setComplete(false);
        driverCertifyMapper.updateById(driverCertify);
        return driverCertify;
    }
}
