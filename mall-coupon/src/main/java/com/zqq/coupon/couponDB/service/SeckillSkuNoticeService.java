package com.zqq.coupon.couponDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.coupon.couponDB.entity.SeckillSkuNoticeEntity;

import java.util.Map;

/**
 * 秒杀商品通知订阅
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:03:48
 */
public interface SeckillSkuNoticeService extends IService<SeckillSkuNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

