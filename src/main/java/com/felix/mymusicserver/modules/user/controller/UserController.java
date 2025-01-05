package com.felix.mymusicserver.modules.user.controller;

import com.felix.mymusicserver.common.exception.BusinessException;
import com.felix.mymusicserver.common.response.Result;
import com.felix.mymusicserver.common.utils.FileUtils;
import com.felix.mymusicserver.modules.user.dto.LoginDTO;
import com.felix.mymusicserver.modules.user.dto.UpdatePasswordDTO;
import com.felix.mymusicserver.modules.user.dto.UserRegisterDTO;
import com.felix.mymusicserver.modules.user.dto.UserUpdateDTO;
import com.felix.mymusicserver.modules.user.entity.User;
import com.felix.mymusicserver.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Tag(name = "用户管理")
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final FileUtils fileUtils;  // 新增

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(token);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    @Operation(summary = "更新VIP状态")
    @PostMapping("/{userId}/vip")
    public Result<Void> updateVipStatus(@PathVariable Long userId, @RequestParam Boolean isVip) {
        userService.updateVipStatus(userId, isVip);
        return Result.success();
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody @Valid UserUpdateDTO updateDTO) {
        userService.updateUserInfo(updateDTO);
        return Result.success();
    }

    @Operation(summary = "更新头像")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Parameter(name = "file", description = "Base64格式的图片，不需要前缀", required = true)
    public Result<String> updateAvatar(@RequestPart("file") String base64File) {
        try {
            // 验证并处理base64字符串
            if (base64File == null || base64File.isEmpty()) {
                throw new BusinessException("文件不能为空");
            }

            // 去除base64前缀（如果有的话）
            if (base64File.contains(",")) {
                base64File = base64File.substring(base64File.indexOf(",") + 1);
            }

            // 转换为MultipartFile
            MultipartFile file = fileUtils.base64ToMultipartFile(base64File);

            return Result.success(userService.updateAvatar(file));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("头像上传失败", e);
            throw new BusinessException("头像上传失败");
        }
    }


//    @Operation(summary = "更新头像")
//    @PostMapping("/avatar")
//    public Result<String> updateAvatar(@RequestParam(value = "file") String base64File) {
//        try {
//            // 处理Base64文件
//            String[] parts = base64File.split(",");
//            String imageString = parts.length > 1 ? parts[1] : parts[0];
//
//            // 解码Base64
//            byte[] imageBytes = Base64.getDecoder().decode(imageString);
//
//            // 创建临时文件
//            MultipartFile multipartFile = new MockMultipartFile(
//                    "file",
//                    "temp.jpg",
//                    "image/jpeg",
//                    imageBytes
//            );
//
//            return Result.success(userService.updateAvatar(multipartFile));
//        } catch (Exception e) {
//            log.error("头像上传失败", e);
//            throw new BusinessException("头像上传失败");
//        }
//    }
}