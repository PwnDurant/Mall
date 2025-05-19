package com.zqq.product.feign;

import com.zqq.common.to.SkuReductionTO;
import com.zqq.common.to.SpuBoundsTO;
import com.zqq.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("mall-coupon")
public interface CouponFeignService {


    /**
     * 调用远程方法
     * 1）将对象转为JSON
     * 2）找到远程服务，会把转的对象放在请求体位置发送请求
     * 3）对方服务收到请求，收到请求体里面的JSON数据
     * 4）对方服务也有 @RequestBody 会将请求体里面的数据转为 SpuBoundsEntity
     * 5）只要JSON数据模型是兼容的。双方服务无需使用同一个 TO
     * @param spuBoundsTO
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTO spuBoundsTO);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);
}
