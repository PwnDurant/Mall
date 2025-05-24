package com.zqq.product.productDB.service.impl;

import com.zqq.product.productDB.entity.SkuImagesEntity;
import com.zqq.product.productDB.entity.SpuInfoDescEntity;
import com.zqq.product.productDB.service.AttrGroupService;
import com.zqq.product.productDB.service.SkuImagesService;
import com.zqq.product.productDB.service.SpuInfoDescService;
import com.zqq.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.SkuInfoDao;
import com.zqq.product.productDB.entity.SkuInfoEntity;
import com.zqq.product.productDB.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String)params.get("key");
        String catelogId = (String)params.get("catelogId");
        String brandId = (String)params.get("brandId");
        String min = (String)params.get("min");
        String max = (String)params.get("max");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and(w->{
               w.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        if (!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("catalogId",catelogId);
        }
        if (!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }
        if (!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }
        if (!StringUtils.isEmpty(max)){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);
                if(bigDecimal.compareTo(new BigDecimal("0"))>0){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){
                log.error("传入的数据有问题");
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据 spuId 查询所有 sku 信息
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
    }

    /**
     * 查询商品详情
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
//        1，sku基本信息 pms_sku_info
        SkuInfoEntity info = getById(skuId);
        skuItemVo.setInfo(info);

//        2，sku 图片信息 pms_sku_images
        List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(images);

//        3，获取 spu 的销售属性组合

//        4，获取 spu 的介绍
        Long spuId = info.getSpuId();
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDesc(spuInfoDescEntity);

//        5，获取 spu 的规格参数信息

        skuItemVo.setGroupAttrs();

        return null;
    }

}