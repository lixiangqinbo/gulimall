package com.lxsx.gulimall.product.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxsx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxsx.gulimall.product.vo.AttrAttrgroupRelationEntityVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    AttrAttrgroupRelationEntity selectByAttrId(@Param("attrId") Long attrId);
    List<AttrAttrgroupRelationEntity> selectByAttrId1(@Param("attrId") Long attrId);

    void updateByAttrId(@Param("attrId") Long attrId, @Param("attrGroupId")Long attrGroupId);

    void deleteBatchByAttrId(@Param("asList") List<Long> asList);

    IPage<AttrAttrgroupRelationEntity> selectByAttrGroupId(IPage<AttrAttrgroupRelationEntity> page,@Param("attrgroupId")Long attrgroupId);

    void deleteByAttrIdsAndGroupIds(@Param("arList")AttrAttrgroupRelationEntityVo[] attrAttrgroupRelationEntityVo);

    void inserBatch(@Param("arList")AttrAttrgroupRelationEntityVo[] attrAttrgroupRelationEntityVo);

    List<AttrAttrgroupRelationEntity> selectByGroupIds(@Param("groupIds") List<Long> groupIds);
}


