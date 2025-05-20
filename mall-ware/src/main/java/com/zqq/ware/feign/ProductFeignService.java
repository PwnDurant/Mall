package com.zqq.ware.feign;


import com.zqq.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * /product/skuinfo/info/{skuId}
     * /api/product/skuinfo/info/{skuId}
     *
     * 1)让所有请求过Gateway
     * 2)直接给后台服务发请求
     * 远程获取某一个SKU的信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

}
