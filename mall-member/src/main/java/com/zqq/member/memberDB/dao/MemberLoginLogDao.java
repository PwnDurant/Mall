package com.zqq.member.memberDB.dao;

import com.zqq.member.memberDB.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author chase
 * @email chase@gmail.com
 * @date 2025-05-14 10:17:58
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
