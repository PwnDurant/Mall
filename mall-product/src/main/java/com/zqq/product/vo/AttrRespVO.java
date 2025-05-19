package com.zqq.product.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttrRespVO extends AttrVO{

    /**
     * 所属分类的名字
     */
    private String catelogName;

    /**
     * 所属分组的名字
     */
    private String groupName;

    private Long[] catelogPath;

}
