package com.zqq.order.orderDB.dao;

import com.zqq.order.orderDB.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:23:25
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
