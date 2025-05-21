package com.zqq.product.productDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.product.productDB.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:23
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);
}

