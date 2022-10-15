package com.lxsx.gulimall.coupon.dao;

import com.lxsx.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀活动商品关联
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:01:12
 */
@Mapper
public interface SeckillSkuRelationDao extends BaseMapper<SeckillSkuRelationEntity> {

    List<SeckillSkuRelationEntity> selectBySessinIds(@Param("killSessinIds") List<Long> killSessinIds);
}
