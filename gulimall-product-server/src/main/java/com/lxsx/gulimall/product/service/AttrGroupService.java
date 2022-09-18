package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.product.entity.AttrEntity;
import com.lxsx.gulimall.product.vo.AttrAttrgroupRelationEntityVo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.product.entity.AttrGroupEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageCommon(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);


    PageUtils queryLinkAttr(Long attrgroupId);

    void removeAttrRelation(AttrAttrgroupRelationEntityVo[] attrAttrgroupRelationEntityVo);

    List<AttrEntity> queryNoRelationAttr(Long attrgroupId);
}

