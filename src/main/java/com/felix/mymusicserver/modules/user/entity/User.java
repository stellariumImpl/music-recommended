package com.felix.mymusicserver.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.felix.mymusicserver.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String email;

    private String phone;

    private Integer gender;

    private Integer status;

    private Integer isVip;

    private LocalDateTime vipExpireTime;

    private LocalDateTime lastLoginTime;
}