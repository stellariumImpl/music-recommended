package com.felix.mymusicserver.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.felix.mymusicserver.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}