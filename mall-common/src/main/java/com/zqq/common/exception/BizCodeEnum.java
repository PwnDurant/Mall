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
    NULL_EXCEPTION(10002,"空指针异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),

    PURCHASE_BIND(20000,"该采购单已经被领取,不能进行合并"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private final int code;
    private final String msg;




}
