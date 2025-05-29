package com.zqq.coupon.couponDB.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zqq.coupon.couponDB.entity.SeckillSessionEntity;
import com.zqq.coupon.couponDB.service.SeckillSessionService;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.R;
import org.w3c.dom.stylesheets.LinkStyle;


/**
 * 秒杀活动场次
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:03:48
 */
@RestController
@RequestMapping("coupon/seckillsession")
public class SeckillSessionController {
    @Autowired
    private SeckillSessionService seckillSessionService;


    @GetMapping("/Lates3DaysSession")
    public R getLates3DaysSession(){
        List<SeckillSessionEntity> sessionEntities=seckillSessionService.getLates3DaysSession();
        return R.ok().setData(sessionEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = seckillSessionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SeckillSessionEntity seckillSession = seckillSessionService.getById(id);

        return R.ok().put("seckillSession", seckillSession);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SeckillSessionEntity seckillSession){
		seckillSessionService.save(seckillSession);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SeckillSessionEntity seckillSession){
		seckillSessionService.updateById(seckillSession);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		seckillSessionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
