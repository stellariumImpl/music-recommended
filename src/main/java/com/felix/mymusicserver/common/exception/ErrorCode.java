// ErrorCode.java
package com.felix.mymusicserver.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 系统级别错误
    SYSTEM_ERROR(50000, "系统错误"),
    PARAMETER_ERROR(40000, "参数错误"),
    UNAUTHORIZED(40100, "未授权"),
    FORBIDDEN(40300, "禁止访问"),
    NOT_FOUND(40400, "资源不存在"),

    // 用户模块错误 (1001-1999)
    USER_NOT_FOUND(10001, "用户不存在"),
    USERNAME_EXISTS(10002, "用户名已存在"),
    PASSWORD_ERROR(10003, "密码错误"),
    INVALID_TOKEN(10004, "无效的token"),
    TOKEN_EXPIRED(10005, "token已过期");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}