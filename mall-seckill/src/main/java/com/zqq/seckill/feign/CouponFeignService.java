package com.zqq.seckill.feign;


import com.zqq.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("mall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/Lates3DaysSession")
    R getLates3DaysSession();

}
