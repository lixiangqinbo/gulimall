package com.lxsx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.ware.entity.WareInfoEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    BigDecimal queryFare(Long memberId);
}

