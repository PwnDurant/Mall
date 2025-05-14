package com.zqq.order.orderDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.order.orderDB.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:23:25
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

