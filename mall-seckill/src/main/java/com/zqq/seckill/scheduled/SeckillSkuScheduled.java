package com.zqq.seckill.scheduled;

import com.zqq.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 秒杀商品的定时上架
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SeckillService seckillService;

    private static final String upload_lock="seckill:upload:lock";

    // TODO 幂等性处理
    @Scheduled(cron = "0 0 3 * * ?")   //每天晚上3点
    public void uploadSeckillSkuLatest3Days(){
//        1，重复上架无需处理
        log.info("上架秒杀到商品信息......");
//        分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }


}
