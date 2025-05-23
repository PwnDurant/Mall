package com.zqq.elasticsearch.vo;

import lombok.Data;

import java.util.List;


/**
 * 封装页面所有可能传递过来的检索条件
 */
@Data
public class SearchParam {

    private String keyword; //全文匹配关键字

    private Long catalog3Id; //三级分类Id

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort; //排序条件

    /**
     * 很多过滤条件
     * hasStock（是否有货），skuPrice（价格区间），brandId，catalog3Id，attrs
     * hasStock=0/1
     * skuPrice=1_500/_500/500_
     * brandId=1&brandId=2
     * attrs=1_其他:安卓&attrs=2_5寸:6寸
     */
    private Integer hasStock; //是否有货

    private String skuPrice; //价格区间

    private List<Long> brandId;  //按照品牌选择,可以多选

    private List<String> attrs; //按照属性进行查询

    private Integer pageNum; //页码

}
