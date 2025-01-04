package com.felix.mymusicserver.common.utils;

import com.felix.mymusicserver.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Base64;
import java.util.UUID;

@Component
public class FileUtils {

    public static MultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStrs = base64.split(",");
            byte[] bytes = Base64.getDecoder().decode(baseStrs.length > 1 ? baseStrs[1] : baseStrs[0]);

            return new MockMultipartFile(
                    "file",
                    "temp.jpg",
                    "image/jpeg",
                    bytes
            );
        } catch (Exception e) {
            throw new BusinessException("文件格式错误");
        }
    }

    public static String getContentType(String base64) {
        try {
            String[] parts = base64.split(",");
            if (parts.length > 1) {
                String contentType = parts[0].split(":")[1].split(";")[0];
                return contentType;
            }
            return "image/jpeg"; // 默认类型
        } catch (Exception e) {
            return "image/jpeg";
        }
    }

    public MultipartFile base64ToMultipartFile(String base64) {
        try {
            // 验证 Base64 字符串是否为空
            if (base64 == null || base64.isEmpty()) {
                throw new IllegalArgumentException("Base64 数据不能为空");
            }

            // 限制 Base64 数据的大小（例如：最大 2MB）
            if (base64.length() > 2 * 1024 * 1024) { // Base64 字符串长度
                throw new IllegalArgumentException("文件大小不能超过 2MB");
            }

            // 解码 Base64
            byte[] decodedBytes = Base64.getDecoder().decode(base64);

            // 动态生成文件名
            String fileName = "avatar-" + UUID.randomUUID() + ".jpg"; // 这里默认是 jpg 格式

            // 默认 MIME 类型
            String contentType = "image/jpeg";

            // 返回 MultipartFile
            return new MockMultipartFile(
                    "file",         // 表单名称
                    fileName,       // 文件名
                    contentType,    // 文件类型
                    decodedBytes    // 文件字节
            );
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的文件格式: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("文件处理失败");
        }
    }
}