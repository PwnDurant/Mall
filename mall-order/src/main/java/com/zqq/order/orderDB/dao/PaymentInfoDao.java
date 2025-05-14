package com.zqq.order.orderDB.dao;

import com.zqq.order.orderDB.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:23:23
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
