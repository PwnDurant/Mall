package com.zqq.elasticsearch.controller;

import com.zqq.common.es.SkuEsModel;
import com.zqq.common.exception.BizCodeEnum;
import com.zqq.common.utils.R;
import com.zqq.elasticsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

//    上架商品
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels)  {
        boolean b=false;
        try{
             b= productSaveService.productStatusUp(skuEsModels);
        }catch (Exception e){
            log.error("商品上架错误:{}",e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
        }

        if(!b) return R.ok();
        else return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
    }

}
