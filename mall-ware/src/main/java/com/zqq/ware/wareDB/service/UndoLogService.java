package com.zqq.ware.wareDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.ware.wareDB.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:37:51
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

