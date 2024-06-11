package com.ruiyue.usercenterbackend.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统错误码定义
 */
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(50000, "服务器内部错误"),

    BAD_REQUEST_PARAM(40001, "请求参数错误"),
    USER_NOT_FOUND(40002, "用户不存在"),
    USER_NOT_LOGIN(40003, "用户未登录"),
    USER_LOGIN_FAILED(40004, "用户名或密码错误"),
    USER_LOGIN_FAILED_TOO_MANY_TIMES(40005, "用户登录失败次数过多，请稍后再试"),
    NO_AUTHORITY(40006, "没有权限"),
    ;

    private final int code;
    private final String message;
    private String description ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }


}
