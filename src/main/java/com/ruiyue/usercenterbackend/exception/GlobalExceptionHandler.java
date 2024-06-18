package com.ruiyue.usercenterbackend.exception;

import com.ruiyue.usercenterbackend.common.BaseResponse;
import com.ruiyue.usercenterbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(BizException.class)
    public BaseResponse handleException(BizException e){
        log.error("bizException>>>",e);

        return ResultUtils.fail(e.getMessage(),e.getCode(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleException(RuntimeException e){
        log.error("runtimeException>>>",e);

        return ResultUtils.fail(e.getMessage(),ErrorCode.SYSTEM_ERROR);
    }
}
