package com.pzhu.substitute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.ResultCode;
import com.pzhu.substitute.entity.DriverInfo;
import com.pzhu.substitute.entity.dto.UserRegisterDTO;
import com.pzhu.substitute.mapper.DriverInfoMapper;
import com.pzhu.substitute.mapper.DriverMapper;
import com.pzhu.substitute.service.DriverService;
import com.pzhu.substitute.utils.MyUtil;
import com.pzhu.substitute.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author dengyiqing
 * @description (DriverInfo)表服务实现类
 * @date 2022-01-15 01:45:37
 */
@Service("driverService")
@Slf4j
public class DriverServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Result register(UserRegisterDTO userRegisterDTO) {
        String phoneNum = userRegisterDTO.getPhoneNum();
        if (!MyUtil.checkIsNotNull(phoneNum, userRegisterDTO.getPassword(), userRegisterDTO.getVerify()) ||
                userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
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
        // BeanUtils.copyProperties(userRegisterDTO, userInfo);
        driverInfo.setPhoneNum(phoneNum);
        driverInfo.setSex(1);
        driverInfo.setAge(18);
        driverInfo.setEnabled(false);
        driverInfo.setLocked(false);
        driverInfo.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        int insert = driverMapper.insert(driverInfo);
        return insert == 1 ? new Result(ResultCode.CREATED) : new Result(ResultCode.FAIL);
    }
}

