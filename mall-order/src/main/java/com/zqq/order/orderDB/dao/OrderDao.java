package com.zqq.order.orderDB.dao;

import com.zqq.order.orderDB.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:23:25
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
