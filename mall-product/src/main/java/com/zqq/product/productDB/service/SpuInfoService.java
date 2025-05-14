package com.zqq.product.productDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.product.productDB.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:23
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

