package com.zqq.product.feign.fallback;

import com.zqq.common.exception.BizCodeEnum;
import com.zqq.common.utils.R;
import com.zqq.product.feign.SeckillService;
import org.springframework.stereotype.Component;



@Component
public class SeckillFeignServiceFallBack implements SeckillService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        return R.error(BizCodeEnum.TOO_MANY_REQUEST);
    }
}
