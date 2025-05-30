package com.zqq.ware.wareDB.dao;

import com.zqq.ware.wareDB.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:37:50
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    Long getSkuStock(@Param("sku") Long sku);
}
