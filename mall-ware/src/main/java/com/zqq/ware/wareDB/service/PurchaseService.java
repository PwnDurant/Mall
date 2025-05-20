package com.zqq.ware.wareDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.ware.vo.MergeVO;
import com.zqq.ware.vo.PurchaseDoneVO;
import com.zqq.ware.wareDB.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:37:51
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnReceive(Map<String, Object> params);

    void mergePurchase(MergeVO mergeVO);

    void received(List<Long> ids);

    void done(PurchaseDoneVO purchaseDoneVO);
}

