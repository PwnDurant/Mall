package com.zqq.product.productDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.product.productDB.entity.SpuImagesEntity;

import java.util.Map;

/**
 * spu图片
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:23
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

