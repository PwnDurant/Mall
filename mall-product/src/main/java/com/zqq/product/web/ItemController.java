package com.zqq.product.web;


import com.zqq.product.productDB.service.SkuInfoService;
import com.zqq.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 展示当前 sku 的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        System.out.println("准备查询"+skuId+"详情");
        SkuItemVo skuItemVo=skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }

}
