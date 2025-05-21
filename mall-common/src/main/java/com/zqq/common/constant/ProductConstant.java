package com.zqq.common.constant;

import lombok.*;


public class ProductConstant {

    @AllArgsConstructor
    @Getter
    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");

        private final int code;
        private final String msg;
    }

    @AllArgsConstructor
    @Getter
    public enum StatusEnum{
        NEW_SPU(0,"新建"),
        SPU_UP(1,"商品上架"),
        SPU_DOWN(2,"商品下架");

        private final int code;
        private final String msg;
    }

}
