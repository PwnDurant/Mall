package com.zqq.ware.wareDB.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zqq.ware.vo.SkuHasStockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zqq.ware.wareDB.entity.WareSkuEntity;
import com.zqq.ware.wareDB.service.WareSkuService;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.R;



/**
 * 商品库存
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:37:50
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 查询 SKU 是否有库存
     */
    @PostMapping("/hasStock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds){
//        sku_is,stock
        List<SkuHasStockVO> vos=wareSkuService.getSkusHasStock(skuIds);

        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
