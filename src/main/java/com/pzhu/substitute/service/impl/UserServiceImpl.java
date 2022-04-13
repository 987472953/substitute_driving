package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.ResultCode;
import com.pzhu.substitute.entity.LoginUser;
import com.pzhu.substitute.entity.UserInfo;
import com.pzhu.substitute.entity.dto.UserInfoDTO;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;
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

import javax.annotation.Resource;

/**
 * @author dengyiqing
 * @description 用户管理service实现
 * @date 2022/1/14
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserInfo> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommonService commonService;

    @Override
    public Result logout() {
        log.debug("[退出登录]获得SecurityContext中的用户Id");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String phoneNum = loginUser.getUserInfo().getPhoneNum();
        String redisKey = CommonConstants.USER_PREFIX + phoneNum + CommonConstants.LOGIN_SUFFIX;
        redisUtil.del(redisKey);
        return Result.ok().message("成功退出登录");
    }

    @Override
    @Transactional
    public Result register(UserRegisterDTO userRegisterDTO) {
        String phoneNum = userRegisterDTO.getPhoneNum();
        if (!MyUtil.checkIsNotNull(phoneNum, userRegisterDTO.getPassword(), userRegisterDTO.getVerify()) ||
                userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
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
        return Result.ok().data("successfulUpdates", i);
    }

}
