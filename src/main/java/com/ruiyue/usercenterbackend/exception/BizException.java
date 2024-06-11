package com.ruiyue.usercenterbackend.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常
 */
@Slf4j
@Getter
public class BizException extends RuntimeException{

    private final int code;
    private final String description;
    
    
    public BizException(String message, int code, String description){
        super(message);
        this.code = code;
        this.description = description;
    }

    public BizException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }


}
