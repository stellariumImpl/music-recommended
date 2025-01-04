// UserServiceImpl.java
package com.felix.mymusicserver.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.felix.mymusicserver.common.exception.BusinessException;
import com.felix.mymusicserver.common.exception.ErrorCode;
import com.felix.mymusicserver.common.utils.FileValidationUtil;
import com.felix.mymusicserver.common.utils.OSSUtil;
import com.felix.mymusicserver.modules.user.dto.LoginDTO;
import com.felix.mymusicserver.modules.user.dto.UpdatePasswordDTO;
import com.felix.mymusicserver.modules.user.dto.UserRegisterDTO;
import com.felix.mymusicserver.modules.user.dto.UserUpdateDTO;
import com.felix.mymusicserver.modules.user.entity.User;
import com.felix.mymusicserver.modules.user.mapper.UserMapper;
import com.felix.mymusicserver.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import com.felix.mymusicserver.common.security.SecurityUtils;
import com.felix.mymusicserver.common.security.jwt.JwtTokenProvider;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;  // 添加这行

    private final FileValidationUtil fileValidationUtil;  // 添加这行
    private final OSSUtil ossUtil;  // 添加这行

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, registerDTO.getUsername()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);

        // 保存用户
        this.save(user);
    }

    @Override
    public String login(LoginDTO loginDTO) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 生成 token
        String token = tokenProvider.generateToken(user.getUsername());

        // 更新登录时间
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);

        return token;
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVipStatus(Long userId, boolean isVip) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setIsVip(isVip ? 1 : 0);
        if (isVip) {
            user.setVipExpireTime(LocalDateTime.now().plusYears(1));
        } else {
            user.setVipExpireTime(null);
        }

        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UserUpdateDTO updateDTO) {
        // 获取当前用户
        User user = getCurrentUser();

        // 更新信息
        user.setNickname(updateDTO.getNickname());
        user.setEmail(updateDTO.getEmail());
        user.setPhone(updateDTO.getPhone());

        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        User user = getCurrentUser();

        // 验证旧密码
        if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateAvatar(MultipartFile file) {
        // 验证文件
        fileValidationUtil.validateImageFile(file);

        try {
            // 上传到OSS
            String avatarUrl = ossUtil.uploadFile(file, "avatars");

            // 更新用户信息
            User currentUser = getCurrentUser();
            currentUser.setAvatar(avatarUrl);
            this.updateById(currentUser);

            return avatarUrl;
        } catch (Exception e) {
            log.error("头像上传失败", e);
            throw new BusinessException("头像上传失败");
        }
    }
}