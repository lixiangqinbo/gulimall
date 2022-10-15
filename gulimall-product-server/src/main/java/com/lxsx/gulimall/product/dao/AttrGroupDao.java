package com.lxsx.gulimall.product.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxsx.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxsx.gulimall.product.vo.AttrAttrgroupRelationEntityVo;
import org.apache.ibatis.annotations.Mapper;
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
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    Page<AttrGroupEntity> queryPage(IPage<AttrGroupEntity> page,
                                    @Param("params") Map<String, Object> params);


    void inserBatch(@Param("aaglist")AttrAttrgroupRelationEntityVo[] aaglist);
}
