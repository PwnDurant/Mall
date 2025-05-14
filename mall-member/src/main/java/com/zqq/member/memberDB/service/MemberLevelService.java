package com.zqq.member.memberDB.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqq.common.utils.PageUtils;
import com.zqq.member.memberDB.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:17:58
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

