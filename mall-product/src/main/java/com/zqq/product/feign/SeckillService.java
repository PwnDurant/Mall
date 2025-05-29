package com.zqq.product.feign;

import com.zqq.common.utils.R;
import com.zqq.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-seckill",fallback = SeckillFeignServiceFallBack.class)
public interface SeckillService {

    /**
     * 获取当前 sku 的秒杀信息
     * @param skuId
     * @return
     */
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId")Long skuId);

}
