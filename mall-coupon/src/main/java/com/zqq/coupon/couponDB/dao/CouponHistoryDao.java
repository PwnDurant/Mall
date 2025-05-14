package com.zqq.coupon.couponDB.dao;

import com.zqq.coupon.couponDB.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:03:50
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
