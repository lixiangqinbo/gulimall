package com.lxsx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxsx.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.lxsx.gulimall.product.dao.AttrDao;
import com.lxsx.gulimall.product.dao.CategoryDao;
import com.lxsx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.lxsx.gulimall.product.entity.AttrEntity;
import com.lxsx.gulimall.product.vo.AttrAttrgroupRelationEntityVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.AttrGroupDao;
import com.lxsx.gulimall.product.entity.AttrGroupEntity;
import com.lxsx.gulimall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private  AttrGroupDao attrGroupDao;
    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Resource
    private AttrDao attrDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    //查询三分类id的分组信息 包含模糊查询
    @Override
    public PageUtils queryPageCommon(Map<String, Object> params) {
        IPage<AttrGroupEntity>  page =
                attrGroupDao.queryPage(new Query<AttrGroupEntity>().getPage(params), params);
        return new PageUtils(page);
    }
    //查询三分类id的分组信息 包含模糊查询
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        //查询全部
        String key = (String)params.get("key");
        if (catelogId==0) {
            QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq(!StringUtils.isBlank(key),"attr_group_id",key)
                    .or(!StringUtils.isBlank(key))
                    .like(!StringUtils.isBlank(key), "attr_group_name", key);

            IPage<AttrGroupEntity> page  = this.page(new Query<AttrGroupEntity>().getPage(params)
            , queryWrapper);
            return new PageUtils(page);
        }else{

            QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("catelog_id", catelogId);
            if (!StringUtils.isBlank(key)){
                queryWrapper.and(obj->{
                    obj.eq("attr_group_id", key).or()
                            .like("attr_group_name", key);
                });
            }
            IPage<AttrGroupEntity> page  = this.page(new Query<AttrGroupEntity>().getPage(params)
                    , queryWrapper);
            return new PageUtils(page);
        }

    }

    @Override
    public PageUtils queryLinkAttr(Long attrgroupId) {
        Map<String,Object> params = new HashMap<>();
        params.put("attrgroupId",attrgroupId);
        IPage<AttrAttrgroupRelationEntity> page
                = attrAttrgroupRelationDao.selectByAttrGroupId(
                        new Query<AttrAttrgroupRelationEntity>().getPage(params),attrgroupId);
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = page.getRecords();

        if (attrgroupRelationEntities==null||attrgroupRelationEntities.size()==0) {
            return  null;
        }

        List<AttrEntity> attrEntities = attrgroupRelationEntities.stream().map(attrAttrgroupRelationEntity -> {
            Long attrId = attrAttrgroupRelationEntity.getAttrId();
            AttrEntity attrEntity = attrDao.selectById(attrId);

            return attrEntity;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrEntities);

        return pageUtils;
    }

    @Override
    public void removeAttrRelation(AttrAttrgroupRelationEntityVo[] attrAttrgroupRelationEntityVo) {
//        for (AttrAttrgroupRelationEntityVo item : attrAttrgroupRelationEntityVo) {
//            Map<String,Object> params = new HashMap<>();
//            params.put("attr_id",item.getAttrId());
//            params.put("attr_group_id", item.getAttrGroupId());
//            attrAttrgroupRelationDao.deleteByMap(params);
//            params.clear();
//        }
        //优化一次删除
        attrAttrgroupRelationDao.deleteByAttrIdsAndGroupIds(attrAttrgroupRelationEntityVo);

    }

    @Override
    public List<AttrEntity> queryNoRelationAttr(Long attrgroupId) {
        //本目录 下 本分组下所有没有被关联的

        return null;
    }


}