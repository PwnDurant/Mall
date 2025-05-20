package com.zqq.ware.wareDB.service.impl;

import com.zqq.common.utils.R;
import com.zqq.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.ware.wareDB.dao.WareSkuDao;
import com.zqq.ware.wareDB.entity.WareSkuEntity;
import com.zqq.ware.wareDB.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId:1
         * wareId:2
         */
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String)params.get("skuId");
        String wareId = (String)params.get("wareId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
//        1,判断如果还有这个库存记录新增
        List<WareSkuEntity> entities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(entities.isEmpty()){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(0);
//           TODO 远程查询SKU的名字 如果失败，整个事务无需回滚
//            1）catch 异常
//            2）TODO 还可以用什么办法让异常出现之后不回滚
            try{
                R info = productFeignService.info(skuId);
                if(info.getCode()==0){
                    Map<String,Object> data = (Map<String,Object>)info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }
            this.baseMapper.insert(wareSkuEntity);
        }else{
            this.baseMapper.addStock(skuId,wareId,skuNum);
        }
    }

}