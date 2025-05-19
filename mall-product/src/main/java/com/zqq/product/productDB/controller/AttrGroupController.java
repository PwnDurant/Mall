package com.zqq.product.productDB.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zqq.product.productDB.entity.AttrEntity;
import com.zqq.product.productDB.service.AttrAttrgroupRelationService;
import com.zqq.product.productDB.service.AttrService;
import com.zqq.product.productDB.service.impl.CategoryServiceImpl;
import com.zqq.product.vo.AttrGroupRelationVO;
import com.zqq.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zqq.product.productDB.entity.AttrGroupEntity;
import com.zqq.product.productDB.service.AttrGroupService;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.R;



/**
 * 属性分组
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:24
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @PostMapping("/attr/relation")
    public R ddRelation(@RequestBody List<AttrGroupRelationVO> vos){
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }

    /**
     * 通过三级分类id 获得所有分组以及下面所有的属性
     * @param cateId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long cateId){

//        1，查处当前分类下的所有属性分组
//        2，查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVO> vos =attrGroupService.getAttrGroupWithAttrsByCatelogId(cateId);
        return R.ok().put("data",vos);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entities);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String,Object> params){
        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catalogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catalogId")Long catalogId ){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page=attrGroupService.queryPage(params,catalogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();

        Long[] path=categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVO[] vos ){
        attrService.deleteRelation(vos);
        return R.ok();
    }

}
