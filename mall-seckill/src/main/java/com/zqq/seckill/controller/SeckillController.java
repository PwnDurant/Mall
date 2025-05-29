package com.zqq.seckill.controller;

import com.zqq.common.utils.R;
import com.zqq.seckill.service.SeckillService;
import com.zqq.seckill.to.SeckillSkuRedisTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与到秒杀商品信息
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SeckillSkuRedisTO> tos=seckillService.getCurrentSeckillSkus();
        return R.ok().setData(tos);
    }

    /**
     * 获取当前 sku 的秒杀信息
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId")Long skuId){
        SeckillSkuRedisTO info=seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(info);
    }


    @GetMapping("/kill")
    public String seckill(@RequestParam("killed") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model){
//        1,判断是否登入 拦截器实现
        String orderSn=seckillService.seckill(killId,key,num);  //秒杀成功返回订单号
        model.addAttribute("orderSn",orderSn);
        return "success";
    }
}
