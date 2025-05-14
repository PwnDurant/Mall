package com.zqq.coupon.couponDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.coupon.couponDB.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:03:47
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

