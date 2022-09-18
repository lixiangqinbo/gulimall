package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.product.entity.AttrEntity;
import com.lxsx.gulimall.product.vo.AttrEntityVo;
import com.lxsx.gulimall.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageForAttr(Map<String, Object> params);

    void cascadeSave(AttrEntityVo attrEntityVo);

    AttrEntityVo getDetail(Long attrId);

    void cascadeUpdateById(AttrEntityVo attrVo);

    void cascadeRemove(List<Long> asList);
}

