package com.zqq.ware.wareDB.service.impl;

import com.zqq.common.constant.WareConstant;
import com.zqq.common.exception.BizCodeEnum;
import com.zqq.ware.exception.WareException;
import com.zqq.ware.vo.MergeVO;
import com.zqq.ware.vo.PurchaseDoneVO;
import com.zqq.ware.vo.PurchaseItemDoneVO;
import com.zqq.ware.wareDB.entity.PurchaseDetailEntity;
import com.zqq.ware.wareDB.service.PurchaseDetailService;
import com.zqq.ware.wareDB.service.WareInfoService;
import com.zqq.ware.wareDB.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.ware.wareDB.dao.PurchaseDao;
import com.zqq.ware.wareDB.entity.PurchaseEntity;
import com.zqq.ware.wareDB.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnReceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",WareConstant.PurchaseStatusEnum.CREATED.getCode())
                        .or().eq("status",WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())  // 0 和 1 是已经分配但是还没有领取的
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVO mergeVO) {
//        拿到采购单的Id
        Long purchaseId = mergeVO.getPurchaseId();
        if(purchaseId==null){
//            新建一个默认的采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

//         确认采购单状态是 0 或者 1 才可以合并
        PurchaseEntity purchaseEntity1 = this.getById(purchaseId);
        if(purchaseEntity1.getStatus()==WareConstant.PurchaseStatusEnum.CREATED.getCode()||
        purchaseEntity1.getStatus()==WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){
            //        开始合并物品
            List<Long> items = mergeVO.getItems();
            Long finalPurchaseId=purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(i -> {
//            开始创建每一份商品详情表
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(i);
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return detailEntity;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(collect);

            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }else{
            throw new WareException(BizCodeEnum.PURCHASE_BIND);
        }
    }

    /**
     * 领取采购单
     * @param ids 所有采购单的ID
     */
//    当出现 Exception 或其子类异常时，事务会被回滚
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void received(List<Long> ids) {
//        1，确认当前采购单是新建或者已分配状态
//        这时候Java8中引用语法 --> .map(id -> this.getById(id))
        List<PurchaseEntity> collect = ids.stream().map(this::getById).filter(item -> item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .peek(item-> {
                    item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                })
                .collect(Collectors.toList());
//        2，改变采购单的状态
        this.updateBatchById(collect);
//        3，改变采购项的状态
        collect.forEach(item->{
            List<PurchaseDetailEntity> purchaseDetailEntityList=purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = purchaseDetailEntityList.stream().map(entity -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(entity.getId());
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }

    /**
     * 已完成订单
     * @param purchaseDoneVO
     */
    @Override
    public void done(PurchaseDoneVO purchaseDoneVO) {

        Long id = purchaseDoneVO.getId();

//        2，改变采购的状态
        Boolean flag=true;
        List<PurchaseItemDoneVO> items = purchaseDoneVO.getItems();

        List<PurchaseDetailEntity> updates=new ArrayList<>();
        for (PurchaseItemDoneVO item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if(item.getStatus()==WareConstant.PurchaseDetailStatusEnum.ERROR.getCode()){
                flag=false;
                detailEntity.setStatus(item.getStatus());
            }else{
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
//        3，将成功采购的进入库
                PurchaseDetailEntity currentPurchaseDetail = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(currentPurchaseDetail.getSkuId(),currentPurchaseDetail.getWareId(),currentPurchaseDetail.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }

        purchaseDetailService.updateBatchById(updates);

//        1，改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}