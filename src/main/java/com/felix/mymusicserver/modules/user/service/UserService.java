// UserService.java
package com.felix.mymusicserver.modules.user.service;

import com.felix.mymusicserver.modules.user.dto.UpdatePasswordDTO;
import com.felix.mymusicserver.modules.user.dto.UserUpdateDTO;
import com.felix.mymusicserver.modules.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.felix.mymusicserver.modules.user.dto.LoginDTO;
import com.felix.mymusicserver.modules.user.dto.UserRegisterDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     */
    String login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户信息
     */
    User getCurrentUser();

    /**
     * 更新用户VIP状态
     */
    void updateVipStatus(Long userId, boolean isVip);

    void updateUserInfo(UserUpdateDTO updateDTO);

    void updatePassword(UpdatePasswordDTO updatePasswordDTO);

    String updateAvatar(MultipartFile file);
}