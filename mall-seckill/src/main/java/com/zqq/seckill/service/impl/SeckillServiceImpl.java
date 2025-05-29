package com.zqq.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.TypeReference;
import com.zqq.common.utils.R;
import com.zqq.seckill.feign.CouponFeignService;
import com.zqq.seckill.feign.ProductFeignService;
import com.zqq.seckill.service.SeckillService;
import com.zqq.seckill.to.SeckillSkuRedisTO;
import com.zqq.seckill.vo.SeckillSessionWithSkusVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    private static final String SESSIONS_CACHE_PREFIX="seckill:sessions:";
    private static final String SKUKILL_CACHE_PREFIX="seckill:skus:";
    private static final String SKU_STOCK_SEMAPHORE="seckill:stock:";  // + 商品随机码


    /**
     * 上架
     */
    @Override
    public void uploadSeckillSkuLatest3Days() {
//        1,扫描最近三天所有需要秒杀的活动，以及秒杀商品信息
        R session = couponFeignService.getLates3DaysSession();
        if(session.getCode()==0){
//            上架商品
            List<SeckillSessionWithSkusVO> sessionData = session.getData(new TypeReference<List<SeckillSessionWithSkusVO>>() {
            });
//            将上架商品数据缓存到 redis 中
//            缓存活动信息
            saveSessionInfo(sessionData);
//            缓存活动关联的商品信息
            saveSessionSkuInfos(sessionData);
        }
    }


    public List<SeckillSkuRedisTO> blockHandler(BlockException e){
        log.error("原方法被降级了{}",e.getMessage());
        return null;
    }
    /**
     * 返回当前可以秒杀的商品信息
     * @return
     */
    @SentinelResource(value = "getCurrentSeckillSkus",blockHandler = "blockHandler" )
    @Override
    public List<SeckillSkuRedisTO> getCurrentSeckillSkus() {

        try(Entry entry = SphU.entry("seckillSkus")){
//            TODO 业务代码
        }catch (BlockException e){
//            处理
            log.error("资源被限流{}",e.getMessage());
        }

//        确定当前时间属于哪一个秒杀场次

//        获取这个秒杀场次需要的所有信息
    }

    /**
     * 获取当前商品的秒杀信息
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuRedisTO getSkuSeckillInfo(Long skuId) {

//        1，找到所有需要参与秒杀的商品的 key

//        2，


    }

    /**
     * TODO 处理秒杀请求
     *
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @Override
    public String seckill(String killId, String key, Integer num) {
//        1,合法性校验。获取当前秒杀商品的详细信息

        return killId;
    }

    /**
     * 缓存活动信息
     * @param sessions
     */
    private void saveSessionInfo(List<SeckillSessionWithSkusVO> sessions){

    }

    /**
     * 缓存活动关联的商品信息
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SeckillSessionWithSkusVO> sessions){

    }
}
