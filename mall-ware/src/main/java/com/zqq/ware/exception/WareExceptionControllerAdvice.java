package com.zqq.ware.exception;


import com.zqq.common.exception.BizCodeEnum;
import com.zqq.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 集中处理所有 Ware 系统异常
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.zqq.ware.wareDB.controller")
public class WareExceptionControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e){
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION);
    }

    @ExceptionHandler(value = WareException.class)
    public R handlerWareException(WareException e){
        return R.error(e.getCode(),e.getMsg());
    }


}
