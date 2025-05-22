package com.zqq.product.productDB.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.zqq.common.constant.ProductConstant;
import com.zqq.common.es.SkuEsModel;
import com.zqq.common.to.SkuHasStockVO;
import com.zqq.common.to.SkuReductionTO;
import com.zqq.common.to.SpuBoundsTO;
import com.zqq.common.utils.R;
import com.zqq.product.feign.CouponFeignService;
import com.zqq.product.feign.SearchFeignService;
import com.zqq.product.feign.WareFeignService;
import com.zqq.product.productDB.entity.*;
import com.zqq.product.productDB.service.*;
import com.zqq.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

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
        log.warn("保存Spu基本信息成功");
//        保存 Spu 的描述图片:pms_spu_info_desc
        List<String> decript = spuInfoVO.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);
        log.warn("保存Spu描述图片成功");
//        保存 Spu 的图片集:pms_spu_images
        List<String> images = spuInfoVO.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);
        log.warn("保存Spu图片集成功");
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
        log.warn("保存Spu规格参数成功");
//        保存 Spu 的积分信息:mall_sms -> sms_spu_bounds
        Bounds bounds = spuInfoVO.getBounds();
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(bounds,spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundsTO);
        if(r.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }
        log.warn("保存Spu积分信息成功");
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
                log.warn("保存Sku基本信息成功");
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
                log.warn("保存Sku图片信息成功");
//        sku的销售属性信息：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                log.warn("保存Sku销售属性成功");
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
                log.warn("保存Sku优惠信息成功");
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

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    public void upSpu(Long spuId) {
//        所有需要上架的 sku 因为一个 spu 下会有很多 sku

//        组装需要的数据
//        1,查处当前 spuId 对应的所有 sku 信息：品牌名字......
        List<SkuInfoEntity> skus=skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

//            TODO 4,查出当前 sku 的所有可以被用来检索的规格属性，只需要按照 spu 查询一遍就够了
// 1. 获取当前 spu 的所有基础属性（包含是否可被检索）
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
// 2. 提取所有 attrId
        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
// 3. 调用 attrService 查询哪些 attrId 是可被搜索的
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
// 4. 形成一个集合便于快速判断
        HashSet<Long> idSet = new HashSet<>(searchAttrIds);
// 5. 从 baseAttrs 中筛选出那些是“可被搜索”的属性值实体
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                .filter(item -> idSet.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attr = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attr);
                    return attr;
                })
                .collect(Collectors.toList());
//            TODO 1,发送远程调用，储存系统查询是否有库存
        Map<Long, Boolean> stockMap=null;
        try {
            R r = wareFeignService.getSkusHasStock(skuIds);
            TypeReference<List<SkuHasStockVO>> typeReference = new TypeReference<List<SkuHasStockVO>>() {
            };
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
        }catch (Exception e){
            log.error("库存服务查询出现问题,原因:{}",e);
        }

//        2,封装每一个 sku 的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,esModel);
//            skuPrice,skuImage,hasStock,hotScore,brandName,brandImg,catalogName
//            Attrs -> attrId,attrName,attrValue
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
//            设置库存信息
            if(finalStockMap ==null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
//            TODO 2,热度评分。0
            esModel.setHotScore(0L);
//            TODO 3,查询品牌和分类的名字信息
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogName(category.getName());
//            设置检索属性
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());
//        TODO 将数据发送给 ES 进行保存：直接发送给 mall-elasticsearch 服务进行保存
        R r = searchFeignService.productStatusUp(upProducts);
        if(r.getCode()==0){
//            TODO,改变当前上架商品的 spu 的状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
//            远程调用失败
            log.error("远程调用失败");
//            TODO 7,重复调用问题？接口幂等性：重试机制
//            Feign 调用流程
            /**
             * 1，构造请求数据，将对象转换为 JSON
             *      RequestTemplate template = buildTemplateFromArgs.create(argv);
             * 2，发送请求执行（执行成功会解码响应数据）
             *      executeAndDecode(template)
             * 3，执行请求会有重试机制
             *      while(true){
             *      try{
             *          executeAndDecode(template);
             *      }catch(){
             *          try{retryer.continueOrPropagate(e);}catch(){throw ex;}
             *          continue;
             *      }
             *      }
             *
             */
        }
    }


}