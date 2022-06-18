package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.OrderStatus;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.entity.*;
import com.pzhu.substitute.entity.dto.DriverCertifyDTO;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;
import com.pzhu.substitute.mapper.*;
import com.pzhu.substitute.service.DriverService;
import com.pzhu.substitute.utils.MyUtil;
import com.pzhu.substitute.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author dengyiqing
 * @description (DriverInfo)表服务实现类
 * @date 2022-01-15 01:45:37
 */
@Service("driverService")
@Slf4j
public class DriverServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverService {

    private final RedisUtil redisUtil;
    private final DriverMapper driverMapper;
    private final OrderMapper orderMapper;
    private final PasswordEncoder passwordEncoder;
    private final OrderLogMapper orderLogMapper;
    private final DriverBalanceMapper driverBalanceMapper;
    private final DriverCertifyMapper driverCertifyMapper;

    @Autowired
    public DriverServiceImpl(RedisUtil redisUtil, DriverMapper driverMapper, OrderMapper orderMapper, PasswordEncoder passwordEncoder, OrderLogMapper orderLogMapper, DriverBalanceMapper driverBalanceMapper, DriverCertifyMapper driverCertifyMapper) {
        this.redisUtil = redisUtil;
        this.driverMapper = driverMapper;
        this.orderMapper = orderMapper;
        this.passwordEncoder = passwordEncoder;
        this.orderLogMapper = orderLogMapper;
        this.driverBalanceMapper = driverBalanceMapper;
        this.driverCertifyMapper = driverCertifyMapper;
    }

    @Override
    public Result register(UserRegisterDTO userRegisterDTO) {
        String phoneNum = userRegisterDTO.getPhoneNum();
        if (!MyUtil.checkIsNotNull(phoneNum, userRegisterDTO.getPassword(), userRegisterDTO.getVerify()) || !userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new BizException(ResultCode.BODY_NOT_MATCH);
        }

        String redis_code = (String) redisUtil.get(CommonConstants.REGISTER_CODE + phoneNum);
        if (!userRegisterDTO.getVerify().equals(redis_code)) {
            log.debug("[驾驶员注册]({})用户验证码抛出异常", phoneNum);
            throw new BizException(ResultCode.CODE_INCORRECT);
        }
        redisUtil.del(redis_code);
        LambdaQueryWrapper<DriverInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DriverInfo::getPhoneNum, phoneNum);
        Integer integer = driverMapper.selectCount(wrapper);
        if (integer != 0) {
            log.debug("[驾驶员注册]({})手机号已被注册抛出异常", phoneNum);
            throw new BizException(ResultCode.USER_ALREADY_EXIST);
        }
        DriverInfo driverInfo = new DriverInfo();
        // 完善的登录 用户可自己传具体数据
        driverInfo.setPhoneNum(phoneNum);
        driverInfo.setSex(1);
        driverInfo.setAge(18);
        driverInfo.setEnabled(true);
        driverInfo.setLocked(false);
        driverInfo.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        int insert = driverMapper.insert(driverInfo);
        driverBalanceMapper.insert(new DriverBalance(driverInfo.getId(), 0.0, null));
        return insert == 1 ? new Result(ResultCode.CREATED) : new Result(ResultCode.FAIL);
    }

    @Override
    public Result certify(DriverCertifyDTO driverCertifyDTO, DriverInfo driverInfo) {
        DriverCertify driverCertify = new DriverCertify();
        BeanUtils.copyProperties(driverCertifyDTO, driverCertify);
        driverCertify.setDriverId(driverInfo.getId());
        driverCertify.setComplete(false);
        driverCertifyMapper.insert(driverCertify);
        return Result.ok();
    }

    @Override
    public boolean canTakeOrder(Long id) {
        String redisKey = CommonConstants.DRIVER_PREFIX + id + CommonConstants.ORDER_PREFIX;
        Object o = redisUtil.get(redisKey);
        if (Objects.isNull(o)) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDriverId, id).eq(Order::getOrderStatus, OrderStatus.ASSIGNED);
            List<Order> orders = orderMapper.selectList(wrapper);
            if (orders.size() == 1) {
                // 这儿只能为1 或 0
                redisUtil.set(redisKey, orders.get(0));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public Result completeTheOrder(Long orderId, DriverInfo driverInfo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, orderId).eq(Order::getDriverId, driverInfo.getId());
        Order order = orderMapper.selectOne(wrapper);
        if (Objects.isNull(order)) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("订单不存在");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setOrderStatus(OrderStatus.COMPLETED);
        int i = orderMapper.updateById(updateOrder);
        if (i == 1) {
            OrderLog orderLog = new OrderLog(null, order.getId(), updateOrder.getOrderStatus(), null);
            orderLogMapper.insert(orderLog);
        }

        DriverBalance driverBalance = driverBalanceMapper.selectById(driverInfo.getId());
        if (Objects.isNull(driverBalance)) {
            DriverBalance insertBalance = new DriverBalance(driverInfo.getId(), order.getActualAmount(), null);
            driverBalanceMapper.insert(insertBalance);
        } else {
            BigDecimal oldBalance = BigDecimal.valueOf(driverBalance.getBalance());
            BigDecimal orderAmount = BigDecimal.valueOf(order.getActualAmount());
            orderAmount = orderAmount.multiply(CommonConstants.ORDER_RATE);
            BigDecimal newBalance = oldBalance.add(orderAmount);
            driverBalance.setBalance(newBalance.doubleValue());
            driverBalanceMapper.updateById(driverBalance);
        }

        String redisKey = CommonConstants.DRIVER_PREFIX + driverInfo.getId() + CommonConstants.ORDER_PREFIX;
        redisUtil.del(redisKey);
        // 删除数据列表
        Set<Object> objects = redisUtil.zGetAll(CommonConstants.ORDER_LIST);
        for (Object object : objects) {
            if(((Order) object).getId().equals(orderId)){
                objects.remove(object);
                break;
            }
        }
        return Result.ok();
    }

    @Override
    public List<Order> getMyOrder(DriverInfo driverInfo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getDriverId, driverInfo.getId());
        List<Order> orders = orderMapper.selectList(wrapper);
        return orders;
    }

    @Override
    public DriverBalance queryBalance(DriverInfo driverInfo) {
        DriverBalance driverBalance = driverBalanceMapper.selectById(driverInfo.getId());
        return driverBalance;
    }
}

