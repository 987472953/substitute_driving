package com.pzhu.substitute.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.entity.Order;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.entity.dto.UserInfoDTO;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;

/**
 * @author dengyiqing
 * @description 用户管理service
 * @date 2022/1/14
 */
public interface UserService extends IService<UserInfo> {

    /**
     * 退出登录
     * @return
     */
    Result logout();

    /**
     * 注册用户
     * @param userRegisterDTO 用户注册的dto
     * @return
     */
    Result register(UserRegisterDTO userRegisterDTO);

    /**
     * 修改用户基本信息
     * @param userInfoDTO
     * @param userInfo
     * @return
     */
    Result updateBasicUserInfo(UserInfoDTO userInfoDTO, UserInfo userInfo);

    /**
     * 查询我的订单信息
     * @param userInfo
     * @return
     */
    Result queryIOrders(UserInfo userInfo);

    /**
     * 评论订单
     * @param orderId
     * @param comment
     * @return
     */
    Result commentOrder(Long orderId, String comment);

    /**
     * 查询订单的代驾员信息
     * @param orderId
     * @param userInfo
     * @return
     */
    Result queryOrderDriver(Long orderId, UserInfo userInfo);

    /**
     * 查询该用户的该订单
     * @param orderId
     * @param userInfo
     * @return
     */
    Order queryUserOrderById(Long orderId, UserInfo userInfo);

    /**
     * 查询订单评论
     * @param orderId
     * @param userInfo
     * @return
     */
    Result queryOrderComment(Long orderId, UserInfo userInfo);

    /**
     * 查询当前的订单
     * @param userInfo
     * @return
     */
    Result queryNowOrder(UserInfo userInfo);
}
