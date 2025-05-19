package com.zqq.coupon.couponDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.to.SkuReductionTO;
import com.zqq.common.utils.PageUtils;
import com.zqq.coupon.couponDB.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:03:47
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuReduction(SkuReductionTO skuReductionTO);
}

