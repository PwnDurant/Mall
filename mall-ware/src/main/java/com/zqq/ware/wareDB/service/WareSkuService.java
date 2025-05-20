package com.zqq.ware.wareDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.ware.wareDB.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:37:50
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);
}

