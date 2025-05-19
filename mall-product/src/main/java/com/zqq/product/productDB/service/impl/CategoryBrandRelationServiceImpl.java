package com.zqq.product.productDB.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zqq.product.productDB.dao.BrandDao;
import com.zqq.product.productDB.dao.CategoryDao;
import com.zqq.product.productDB.entity.BrandEntity;
import com.zqq.product.productDB.entity.CategoryEntity;
import com.zqq.product.productDB.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.CategoryBrandRelationDao;
import com.zqq.product.productDB.entity.CategoryBrandRelationEntity;
import com.zqq.product.productDB.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

//        查询详细名字
        BrandEntity brandEntity = brandDao.selectById(brandId); //根据品牌Id 查到品牌详情。
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId); //根据分类id 查询分类详情

//        将查出的名字设置到关联关系中
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

//        保存
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        this.update(categoryBrandRelationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId,name);
    }

    /**
     * 查询指定分类里面的所有品牌信息
     * @param catId
     * @return
     */
    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
//        根据传入的分类id查询出当前分类下所关联的 分类和品牌信息
        List<CategoryBrandRelationEntity> categoryBrandList = categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));

        List<BrandEntity> brandEntities = categoryBrandList.stream().map(item -> {
            Long brandId = item.getBrandId();
            return brandService.getById(brandId);
        }).collect(Collectors.toList());

        return brandEntities;
    }

}