package com.ruiyue.usercenterbackend.common;

/**
 * 结果返回的工具类
 */
public class ResultUtils {

    /**
     * 成功
     */
   public static final int RESULT_CODE_SUCCESS = 0;

    /**
     * 返回成功的结果
     * @param data
     * @return
     */
    public static BaseResponse success(Object data){
       return new BaseResponse(RESULT_CODE_SUCCESS, data, "ok", "");
    }

    /**
     * 返回失败的结果
     * @return
     */
    public static BaseResponse fail(String msg, int code ,String description){
        return new BaseResponse(code, null, msg, description);
    }
}
