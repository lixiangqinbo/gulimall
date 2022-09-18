package com.lxsx.gulimall.order.dao;

import com.lxsx.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 13:41:32
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
