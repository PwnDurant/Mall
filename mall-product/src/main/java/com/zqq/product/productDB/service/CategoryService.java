package com.zqq.product.productDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.product.productDB.entity.CategoryEntity;

import java.util.Map;

/**
 * 商品三级分类
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:24
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

