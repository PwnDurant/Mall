package com.zqq.coupon.couponDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.coupon.couponDB.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:03:51
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

