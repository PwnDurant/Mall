package com.zqq.product.productDB.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zqq.product.productDB.entity.ProductAttrValueEntity;
import com.zqq.product.productDB.service.ProductAttrValueService;
import com.zqq.product.vo.AttrRespVO;
import com.zqq.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zqq.product.productDB.entity.AttrEntity;
import com.zqq.product.productDB.service.AttrService;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.R;



/**
 * 商品属性
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-13 21:51:24
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 查出商品的规格属性
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities=productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",entities);
    }

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId,@PathVariable("attrType") String attrType){
        PageUtils p=attrService.queryBaseAttrPage(params,catelogId,attrType);
        return R.ok().put("page",p);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVO attrRespVO=attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 更新
     */
    @PostMapping("/update/{spuId}")
    public R updateWithSpuId(@PathVariable("spuId") Long spuId,
                             @RequestBody List<ProductAttrValueEntity> entities){
//        attrService.updateAttr(attr);
        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
