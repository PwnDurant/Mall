package com.zqq.product.exception;

import com.zqq.common.exception.BizCodeEnum;
import com.zqq.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


/**
 * 集中处理所有异常
 */
@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "com.zqq.product.productDB.controller")
@RestControllerAdvice(basePackages = "com.zqq.product.productDB.controller")
public class MallExceptionControllerAdvice {

    /**
     * 集中处理指定异常
     * @param e
     * @return ModelAndView 这是出现异常之后返回的页面
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{},异常类型:{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> map=new HashMap<>();
        bindingResult.getFieldErrors().forEach((error)->{
            map.put(error.getField(), error.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION).put("data",map);
    }

    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e){
        return R.error(BizCodeEnum.VALID_EXCEPTION);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public R handleException(NullPointerException e){
        return R.error(BizCodeEnum.NULL_EXCEPTION);
    }

}
