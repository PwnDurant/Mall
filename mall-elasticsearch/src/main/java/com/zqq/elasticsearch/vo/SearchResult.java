package com.zqq.elasticsearch.vo;

import com.zqq.common.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * 查询出来的返回结果
 */
@Data
public class SearchResult {

    private List<SkuEsModel> products;  //查询到的所有商品信息

    private Integer pageNum; //当前页码

    private Long total; //总的记录树

    private Integer totalPages; //总页码

    private List<BrandVO> brands;  //查询到的结果所有设计到的品牌

    private List<AttrVO> attrs; //当前查询到的结果所涉及到的所有属性

    private List<CatalogVO> catalogs; //当前查询到的结果所涉及到的所有分类

//    =================== 以上就是需要返回给页面的所有信息 ==================

    @Data
    public static class BrandVO{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    private static class AttrVO{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    private static class CatalogVO{
        private Long catalogId;
        private String catalogName;
    }

}
