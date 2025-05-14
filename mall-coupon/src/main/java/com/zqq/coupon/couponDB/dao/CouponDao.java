package com.zqq.coupon.couponDB.dao;

import com.zqq.coupon.couponDB.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:03:51
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
