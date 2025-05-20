package com.zqq.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVO {

    private Long purchaseId;  // 整单集合
    private List<Long> items;  //合并项集合

}
