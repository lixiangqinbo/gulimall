package com.lxsx.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.coupon.dao.SeckillSessionDao;
import com.lxsx.gulimall.coupon.entity.SeckillSessionEntity;
import com.lxsx.gulimall.coupon.service.SeckillSessionService;

@Slf4j
@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> querySecKillSession3day() {
        // 2020-10-06
        LocalDate now = LocalDate.now();
        //叠加2天=第三天 2022-10-08
        LocalDate next3Day = now.plusDays(2);
        //00:00:00
        LocalTime min = LocalTime.MIN;
        //23:59:59
        LocalTime max = LocalTime.MAX;
        //2020-10-06 00:00:00
        LocalDateTime startTime = LocalDateTime.of(now, min);
        //2022-10-08 23:59:59
        LocalDateTime endTime = LocalDateTime.of(next3Day, max);
        String start = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startTime);
        String end = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(endTime);
        log.info("startTime=>"+start);
        log.info("endTime=>"+end);
        List<SeckillSessionEntity> seckillSessionEntities = this.baseMapper.selectList(new LambdaQueryWrapper<SeckillSessionEntity>()
                .between(SeckillSessionEntity::getStartTime, start, end));
        return seckillSessionEntities;
    }

}