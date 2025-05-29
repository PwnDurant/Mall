package com.zqq.seckill.service;


import com.zqq.seckill.to.SeckillSkuRedisTO;

import java.util.List;

public interface SeckillService {


    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTO> getCurrentSeckillSkus();

    SeckillSkuRedisTO getSkuSeckillInfo(Long skuId);

    String seckill(String killId, String key, Integer num);
}
