package com.zqq.product.productDB.dao;

import com.zqq.product.productDB.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:24
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
