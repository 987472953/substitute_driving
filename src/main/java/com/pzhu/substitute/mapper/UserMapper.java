package com.pzhu.substitute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pzhu.substitute.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author dengyiqing
 * @description 用户管理dao
 * @date 2022/1/12
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {

    List<String> queryPermissionsByUserId(Long userId);


    int createUserRole(Long id);
}
