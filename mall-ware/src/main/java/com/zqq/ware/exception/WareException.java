package com.zqq.ware.exception;

import com.zqq.common.constant.WareConstant;
import com.zqq.common.exception.BizCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WareException extends RuntimeException{

//    状态码
    private Integer code;

//    错误信息
    private String msg;

    public WareException(BizCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }
}
