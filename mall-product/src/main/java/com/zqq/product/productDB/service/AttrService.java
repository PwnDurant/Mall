package com.zqq.product.productDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.product.productDB.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:24
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

