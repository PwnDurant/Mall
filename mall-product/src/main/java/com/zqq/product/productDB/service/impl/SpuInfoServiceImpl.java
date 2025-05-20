package com.zqq.product.productDB.service.impl;

import com.alibaba.nacos.client.utils.StringUtils;
import com.zqq.common.to.SkuReductionTO;
import com.zqq.common.to.SpuBoundsTO;
import com.zqq.common.utils.R;
import com.zqq.product.feign.CouponFeignService;
import com.zqq.product.productDB.entity.*;
import com.zqq.product.productDB.service.*;
import com.zqq.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *  TODO 还有其他优化点，后续完善
     * @param spuInfoVO
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfoVO) {

//        保存 Spu 基本信息:pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
//        保存 Spu 的描述图片:pms_spu_info_desc
        List<String> decript = spuInfoVO.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);
//        保存 Spu 的图片集:pms_spu_images
        List<String> images = spuInfoVO.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);
//        保存 Spu 的规格参数:pms_product-attr_value
        List<BaseAttrs> baseAttrs = spuInfoVO.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity byId = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
//        保存 Spu 的积分信息:mall_sms -> sms_spu_bounds
        Bounds bounds = spuInfoVO.getBounds();
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(bounds,spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundsTO);
        if(r.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }

//        保存当前 Spu 对应的所有 sku 信息
//        sku的基本信息：名字，标题... -> pms_sku_info
        List<Skus> skus = spuInfoVO.getSkus();
        if(!skus.isEmpty()){
            skus.forEach(item->{
                String defaultImg="";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg()==1) {
                        defaultImg=image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity ->{
//                    返回true就是需要，返回false 就会被剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
//        sku的图片信息：pms_sku_images
                skuImagesService.saveBatch(imagesEntities);
//                TODO 没有图片路径的不需要保存

                List<Attr> attrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
//        sku的销售属性信息：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
//        sku的优惠信息，满减信息：mall_sms -> sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(item,skuReductionTO);
                skuReductionTO.setSkuId(skuId);
                if(skuReductionTO.getFullCount()>0 || (skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) > 0)){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTO);
                    if(r1.getCode()!=0){
                        log.error("远程保存spu优惠信息失败");
                    }
                }

            });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key=(String) params.get("key");
        String status=(String) params.get("status");
        String brandId=(String) params.get("brandId");
        String catelogId=(String) params.get("catelogId");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
//            为了避免逻辑歧义，当多个条件想组合成一个逻辑的时候，由于 or 的优先级高 最好用 lambda 表达式的写法
//            status = 1 AND (id = 1 or spu_name like xxx)
//            status = 1 AND id = 1 or spu_name like xxx
        }
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalogId",catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }


}