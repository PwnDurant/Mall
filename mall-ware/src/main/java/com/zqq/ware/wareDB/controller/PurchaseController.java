package com.zqq.ware.wareDB.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zqq.ware.vo.MergeVO;
import com.zqq.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zqq.ware.wareDB.entity.PurchaseEntity;
import com.zqq.ware.wareDB.service.PurchaseService;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.R;



/**
 * 采购信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:37:51
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 已完成采购单
     * @param purchaseDoneVO
     * @return
     */
    @PostMapping("/done")
    public R finished(@RequestBody PurchaseDoneVO purchaseDoneVO){
        purchaseService.done(purchaseDoneVO);
        return R.ok();
    }


    /**
     * 领取采购单
     * @param
     * @return
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return R.ok();
    }


    @PostMapping("/merge")
    public R merge(@RequestBody MergeVO mergeVO){
        purchaseService.mergePurchase(mergeVO);
        return R.ok();
    }

    @RequestMapping("/unreceive/list")
    public R unReceiveList(@RequestParam Map<String, Object> params){

        PageUtils page = purchaseService.queryPageUnReceive(params);

        return R.ok().put("page", page);

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
