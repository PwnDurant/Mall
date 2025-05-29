package com.zqq.coupon.couponDB.service.impl;

import com.zqq.coupon.couponDB.entity.SeckillSkuRelationEntity;
import com.zqq.coupon.couponDB.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.coupon.couponDB.dao.SeckillSessionDao;
import com.zqq.coupon.couponDB.entity.SeckillSessionEntity;
import com.zqq.coupon.couponDB.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出最近三天的所有秒杀活动信息
     * @return
     */
    @Override
    public List<SeckillSessionEntity> getLates3DaysSession() {

        LocalDate now = LocalDate.now();
        LocalDate date1 = now.plusDays(1);
        LocalDate date2 = now.plusDays(2);

        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;

        LocalDateTime start = LocalDateTime.of(now, min);
        LocalDateTime end = LocalDateTime.of(date2, max);

        String startFormat=start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endFormat=end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startFormat, endFormat));

        if(!list.isEmpty()){
            List<SeckillSessionEntity> collect = list.stream().peek(session -> {
                Long id = session.getId();
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                session.setRelationSkus(relationEntities);
            }).collect(Collectors.toList());
            return collect;
        }

        return null;

    }

}