package com.zqq.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态码
 */
@Getter
@AllArgsConstructor
public enum BizCodeEnum {



    UNKNOWN_EXCEPTION(10000,"系统未知异常"),

    VALID_EXCEPTION(10001,"参数格式校验失败");

    private final int code;
    private final String msg;




}
