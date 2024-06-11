package com.ruiyue.usercenterbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的接口返回类
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String msg;

    private String description;

    public BaseResponse(int code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }
}
