package com.zqq.product.productDB.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zqq.product.productDB.entity.BrandEntity;
import com.zqq.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zqq.product.productDB.entity.CategoryBrandRelationEntity;
import com.zqq.product.productDB.service.CategoryBrandRelationService;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:24
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 查询当前分类下的所有的品牌
     * 1,Controller 只来处理请求，接收和校验数据
     * 2,Service 来接收Controller的数据进行业务处理
     * 3,Controller 接收Service的数据封装成页面指定的VO
     * @return
     */
    @GetMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId",required = true) Long catId){

        List<BrandEntity> vos=categoryBrandRelationService.getBrandsByCatId(catId);

        List<BrandVO> brandVOList = vos.stream().map(item -> {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(item.getName());
            brandVO.setBrandId(item.getBrandId());
            return brandVO;
        }).collect(Collectors.toList());

        return R.ok().put("data",brandVOList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取当前品牌关联的所有分类接口
     */
    @GetMapping("/catelog/list")
    public R catalogList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data=categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){


		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
