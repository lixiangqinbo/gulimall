package com.lxsx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.ware.entity.WareOrderTaskDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 库存工作单
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<WareOrderTaskDetailEntity> getByTaskId(Long wareOrderTaskEntityId);

}

