package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.OrderStatus;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.common.status.RoleStatus;
import com.pzhu.substitute.entity.*;
import com.pzhu.substitute.entity.dto.UserInfoDTO;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;
import com.pzhu.substitute.mapper.DriverMapper;
import com.pzhu.substitute.mapper.OrderCommentMapper;
import com.pzhu.substitute.mapper.OrderMapper;
import com.pzhu.substitute.mapper.UserMapper;
import com.pzhu.substitute.service.CommonService;
import com.pzhu.substitute.service.UserService;
import com.pzhu.substitute.utils.MyUtil;
import com.pzhu.substitute.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author dengyiqing
 * @description 用户管理service实现
 * @date 2022/1/14
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserInfo> implements UserService {

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;
    private final CommonService commonService;
    private final OrderMapper orderMapper;
    private final OrderCommentMapper orderCommentMapper;
    private final DriverMapper driverMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, AuthenticationManager authenticationManager, RedisUtil redisUtil, PasswordEncoder passwordEncoder, CommonService commonService, OrderMapper orderMapper, OrderCommentMapper orderCommentMapper, DriverMapper driverMapper) {
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.redisUtil = redisUtil;
        this.passwordEncoder = passwordEncoder;
        this.commonService = commonService;
        this.orderMapper = orderMapper;
        this.orderCommentMapper = orderCommentMapper;
        this.driverMapper = driverMapper;
    }

    @Override
    public Result logout() {
        log.debug("[退出登录]获得SecurityContext中的用户Id");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getUserInfo().getId();
        String redisKey = CommonConstants.USER_PREFIX + id + CommonConstants.LOGIN_SUFFIX;
        redisUtil.del(redisKey);
        return Result.ok().message("成功退出登录");
    }

    @Override
    @Transactional
    public Result register(UserRegisterDTO userRegisterDTO) {
        String phoneNum = userRegisterDTO.getPhoneNum();
        if (!MyUtil.checkIsNotNull(phoneNum, userRegisterDTO.getPassword(), userRegisterDTO.getVerify()) ||
                !userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new BizException(ResultCode.BODY_NOT_MATCH);
        }

        String redis_code = (String) redisUtil.get(CommonConstants.REGISTER_CODE + phoneNum);
        if (!userRegisterDTO.getVerify().equals(redis_code)) {
            log.debug("[用户注册]({})用户验证码抛出异常", phoneNum);
            throw new BizException(ResultCode.CODE_INCORRECT);
        }
        redisUtil.del(redis_code);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhoneNum, phoneNum);
        Integer integer = userMapper.selectCount(wrapper);
        if (integer != 0) {
            log.debug("[用户注册]({})手机号已被注册抛出异常", phoneNum);
            throw new BizException(ResultCode.USER_ALREADY_EXIST);
        }
        UserInfo userInfo = new UserInfo();
        // 完善的登录 用户可自己传具体数据
        // BeanUtils.copyProperties(userRegisterDTO, userInfo);
        userInfo.setPhoneNum(phoneNum);
        userInfo.setSex(1);
        userInfo.setAge(18);
        userInfo.setAvatar(CommonConstants.USER_DEFAULT_AVATAR);
        userInfo.setNickname(phoneNum);
        userInfo.setEnabled(true);
        userInfo.setLocked(false);
        userInfo.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        int insert = userMapper.insert(userInfo);
        if (insert == 1) {
            insert = userMapper.createUserRole(userInfo.getId());
        }
        return insert == 1 ? new Result(ResultCode.CREATED) : new Result(ResultCode.FAIL);
    }


    @Override
    public Result updateBasicUserInfo(UserInfoDTO userInfoDTO, UserInfo userInfo) {
        UserInfo updateUser = new UserInfo();
        BeanUtils.copyProperties(userInfoDTO, updateUser);
        updateUser.setId(userInfo.getId());
        int i = userMapper.updateById(updateUser);
        UserInfo newUserInfo = userMapper.selectById(userInfo.getId());
        String redisKey = CommonConstants.USER_PREFIX + userInfo.getId() + CommonConstants.LOGIN_SUFFIX;
        LoginUser loginUser = (LoginUser) redisUtil.get(redisKey);
        loginUser.setUserInfo(newUserInfo);
        redisUtil.set(redisKey, loginUser);
        return Result.ok().data("successfulUpdates", i);
    }

    @Override
    public Result queryIOrders(UserInfo userInfo) {
        Long id = userInfo.getId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, id);
        List<Order> orders = orderMapper.selectList(wrapper);
        return Result.ok().data("orderList", orders);
    }

    @Override
    public Result commentOrder(Long orderId, String comment) {
        Order order = orderMapper.selectById(orderId);
        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("订单状态问题");
        }
        Integer integer = orderCommentMapper.selectCount(new LambdaQueryWrapper<OrderComment>().eq(OrderComment::getOrderId, orderId));
        OrderComment orderComment = new OrderComment(null, orderId, integer + 1, comment, RoleStatus.USER, null);
        int insert = orderCommentMapper.insert(orderComment);
        return insert == 1 ? Result.ok() : Result.error();
    }

    @Override
    public Result queryOrderDriver(Long orderId, UserInfo userInfo) {
        Order order = orderMapper.selectById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("订单号错误");
        }
        if (!userInfo.getId().equals(order.getUserId())) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("这不是您的订单");
        }
        DriverInfo driverInfo = driverMapper.selectById(order.getDriverId());
        return Result.ok().data("driverInfo", driverInfo);
    }

    @Override
    public Order queryUserOrderById(Long orderId, UserInfo userInfo) {

        Order order = orderMapper.selectById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userInfo.getId())) {
            throw new BizException(ResultCode.UNAUTHORISE);
        }
        return order;
    }

    @Override
    public Result queryOrderComment(Long orderId, UserInfo userInfo) {
        //todo 判断该订单是否为该用户的
        LambdaQueryWrapper<OrderComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderComment::getOrderId, orderId).orderByAsc(OrderComment::getCreateTime);
        List<OrderComment> commentList = orderCommentMapper.selectList(wrapper);
        return Result.ok().data("commentList", commentList);
    }

    @Override
    public Result queryNowOrder(UserInfo userInfo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userInfo.getId()).lt(Order::getOrderStatus, OrderStatus.COMPLETED);
        Order order = orderMapper.selectOne(wrapper);
        return Result.ok().data("nowOrder", order);
    }

}
