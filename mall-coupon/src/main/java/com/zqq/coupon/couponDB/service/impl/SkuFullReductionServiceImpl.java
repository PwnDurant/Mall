package com.zqq.coupon.couponDB.service.impl;

import com.zqq.common.to.MemberPrice;
import com.zqq.common.to.SkuReductionTO;
import com.zqq.coupon.couponDB.entity.MemberPriceEntity;
import com.zqq.coupon.couponDB.entity.SkuLadderEntity;
import com.zqq.coupon.couponDB.service.MemberPriceService;
import com.zqq.coupon.couponDB.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.coupon.couponDB.dao.SkuFullReductionDao;
import com.zqq.coupon.couponDB.entity.SkuFullReductionEntity;
import com.zqq.coupon.couponDB.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuReduction(SkuReductionTO skuReductionTO) {
//        sku的优惠信息，满减信息：mall_sms -> sms_sku_ladder\sms_sku_full_reduction\sms_member_price
//        sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTO.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTO.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTO.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTO.getCountStatus());
        if(skuReductionTO.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }

//      sms_sku_full_reduction
        SkuFullReductionEntity reductionEntity=new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTO,reductionEntity);
        if(reductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0){
            this.save(reductionEntity);
        }

//        sms_member_price
        List<MemberPrice> memberPrices = skuReductionTO.getMemberPrices();
        List<MemberPriceEntity> collect = memberPrices.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTO.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(item-> item.getMemberPrice().compareTo(new BigDecimal("0"))>0).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);
    }

}