package com.ruiyue.usercenterbackend.exception;

import com.ruiyue.usercenterbackend.common.BaseResponse;
import com.ruiyue.usercenterbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(BizException.class)
    public BaseResponse handleException(BizException e){
        log.error("服务运行异常",e);

        return ResultUtils.fail(e.getMessage(),e.getCode(),e.getDescription());
    }


}
