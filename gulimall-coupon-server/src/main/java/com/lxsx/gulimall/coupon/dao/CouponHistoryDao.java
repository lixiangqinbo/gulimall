package com.lxsx.gulimall.coupon.dao;

import com.lxsx.gulimall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:01:13
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
