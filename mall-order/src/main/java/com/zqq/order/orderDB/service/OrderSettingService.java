package com.zqq.order.orderDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.order.orderDB.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:23:24
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

