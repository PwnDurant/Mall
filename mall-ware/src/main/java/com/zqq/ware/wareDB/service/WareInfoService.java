package com.zqq.ware.wareDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.ware.wareDB.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:37:50
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

