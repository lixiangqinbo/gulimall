package com.lxsx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxsx.gulimall.constant.ProductConstant;
import com.lxsx.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.lxsx.gulimall.product.dao.AttrDao;
import com.lxsx.gulimall.product.dao.CategoryDao;
import com.lxsx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.lxsx.gulimall.product.entity.AttrEntity;
import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.vo.AttrAttrgroupRelationEntityVo;
import com.lxsx.gulimall.product.vo.AttrEntityVo;
import com.lxsx.gulimall.product.vo.AttrGroupEntityVo;
import com.lxsx.gulimall.product.vo.skuinfovo.SpuBaseAttrVo;
import com.lxsx.gulimall.product.vo.skuinfovo.SpuItemGroupAttrVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@Slf4j
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private  AttrGroupDao attrGroupDao;
    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Resource
    private AttrDao attrDao;
    @Resource
    private CategoryDao categoryDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    //???????????????id??????????????? ??????????????????
    @Override
    public PageUtils queryPageCommon(Map<String, Object> params) {
        IPage<AttrGroupEntity>  page =
                attrGroupDao.queryPage(new Query<AttrGroupEntity>().getPage(params), params);
        return new PageUtils(page);
    }
    //???????????????id??????????????? ??????????????????
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        //????????????
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
        //??????????????????
        attrAttrgroupRelationDao.deleteByAttrIdsAndGroupIds(attrAttrgroupRelationEntityVo);

    }

    @Override
    public PageUtils queryNoRelationAttr( Map<String,Object> params,Long attrgroupId) {
        /**
         * ???????????????
         * 1. ???????????????attrgroupId??????????????????????????????????????????????????? ???????????? ?????????
         * 2. ???????????????attrgroupId????????????????????????????????????attrId?????????
         */
        if (params==null) {
            params = new HashMap<>();
        }
        //1.??????????????????????????????
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        if (attrGroupEntity==null) {
            throw new RuntimeException("????????????attrgroupId:"+attrgroupId+"??????????????????");
        }
        Long catelogId = attrGroupEntity.getCatelogId();

        //2.???????????????????????????????????????attrId
        Map<String,Object> queryParams = new HashMap<>();
        queryParams.put("attr_group_id", attrgroupId);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities =
                attrAttrgroupRelationDao.selectByMap(queryParams);
        List<Long> attrIds = new ArrayList<>();
        if (attrAttrgroupRelationEntities!=null) {
            //???????????????attrId
             attrIds = attrAttrgroupRelationEntities.stream().map(attrAttrgroupRelationEntity -> {
                return attrAttrgroupRelationEntity.getAttrId();
            }).collect(Collectors.toList());
        }
        queryParams.clear();
        //???????????????????????????????????????
        queryParams.put("catelog_id", catelogId);
//        List<AttrEntity> attrEntities = attrDao.selectByMap(params);
        //?????? ???????????????????????????????????????
        IPage<AttrEntity> page = new Query<AttrEntity>().getPage(params);
        String key = (String) params.get("key");
        IPage<AttrEntity> attrEntities = attrDao.selectPage(page, new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId)
                .like(StringUtils.isNotBlank(key),"attr_name",key));
        List<AttrEntity> attrEntitiesPage = page.getRecords();
        PageUtils pageUtils = new PageUtils(page);
        final List<Long> ATTR_IDS = attrIds;
        //A:????????????????????????????????????????????????????????????
        if (attrAttrgroupRelationEntities!=null||attrAttrgroupRelationEntities.size()==0||ATTR_IDS.size()==0) {
            List<AttrEntity> resAttr = attrEntitiesPage.stream().filter((attrEntity -> {
                return !ATTR_IDS.contains(attrEntity.getAttrId());
            })).collect(Collectors.toList());

            pageUtils.setList(resAttr);
            return pageUtils ;
        }
        //B:???????????????????????????????????????
        pageUtils.setList(attrEntitiesPage);
        return pageUtils;
    }

    @Override
    @Transactional
    public void saveGroupRelationBatch(AttrAttrgroupRelationEntityVo[] attrAttrgroupRelationEntityVo) {

        attrAttrgroupRelationDao.inserBatch(attrAttrgroupRelationEntityVo);
    }

    @Override
    public List<AttrGroupEntityVo> queryGroupWithAttrByCatId(final Integer ATTR_TYPE,Long catelogId) {
        /**
         * ?????????TODO JAVA ???????????? ?????????
         * step1.?????????catelogId ?????????????????????id
         * step2.??????step1?????????id????????????????????????????????????id?????????id
         * step3.??????step2?????????id??????????????????
         */
        // step1.?????????catelogId ?????????????????????id
        List<AttrGroupEntity> attrGroupEntities =
                attrGroupDao.selectList(new LambdaQueryWrapper<AttrGroupEntity>()
                        .eq(AttrGroupEntity::getCatelogId, catelogId));

        if (attrGroupEntities==null||attrGroupEntities.size()==0) {
            return null;
        }
        List<AttrGroupEntityVo> attrGroupEntityVos = attrGroupEntities.stream().map(attrGroupEntity -> {

            AttrGroupEntityVo attrGroupEntityVo = new AttrGroupEntityVo();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupEntityVo);

            //step2.??????step1?????????id????????????????????????????????????id?????????id
            List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities
                    = attrAttrgroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupEntity.getAttrGroupId()));

            if (attrAttrgroupRelationEntities == null || attrAttrgroupRelationEntities.size() == 0) {
                return attrGroupEntityVo;

            }

            List<AttrEntity> attrEntities = attrAttrgroupRelationEntities.stream().map(attrAttrgroupRelationEntity -> {
                //step3.??????step2??????????????????id??????????????????
                // ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType();
                //ProductConstant.AttrEnum.ATTR_TYPE_SALE.getType();
                AttrEntity attrEntity = attrDao.selectByAttrIdAttrType(attrAttrgroupRelationEntity.getAttrId(),
                        ATTR_TYPE);
                return attrEntity;

            }).filter(attrEntity -> { //???????????????
                return (attrEntity!=null);
            }).collect(Collectors.toList());

            attrGroupEntityVo.setAttrs(attrEntities);
            return attrGroupEntityVo;

        }).collect(Collectors.toList());

        return attrGroupEntityVos;
    }

    @Override
    public List<SpuItemGroupAttrVo> queryAttrGropWithAttrsByCatId(Long catalogId,Long spuId) {
       // pms_attr_group
        List<AttrGroupEntity> attrGroupEntities =
                this.baseMapper.selectList(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCatelogId, catalogId));

        List<SpuItemGroupAttrVo> spuItemGroupAttrVos = attrGroupEntities.stream().map(attrGroupEntity -> {
            //????????????,??????id
            SpuItemGroupAttrVo spuItemGroupAttrVo = new SpuItemGroupAttrVo();
            spuItemGroupAttrVo.setGroupName(attrGroupEntity.getAttrGroupName());
            spuItemGroupAttrVo.setGroupId(attrGroupEntity.getAttrGroupId());
            return spuItemGroupAttrVo;
        }).collect(Collectors.toList());

        List<Long> groupIds = attrGroupEntities.stream().map(attrGroupEntity -> {
            return attrGroupEntity.getAttrGroupId();
        }).collect(Collectors.toList());


       final List<SpuBaseAttrVo> spuBaseAttrVos =
                attrDao.selectBaseAttrByGroupIds(groupIds, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType(), spuId);

        List<SpuItemGroupAttrVo> collect1 = spuItemGroupAttrVos.stream().map(spuItemGroupAttrVo -> {
            List<SpuBaseAttrVo> collect = spuBaseAttrVos.stream().filter(spuBaseAttrVo -> {
                if (spuBaseAttrVo.getAttrGroupId() == spuItemGroupAttrVo.getGroupId()) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            spuItemGroupAttrVo.setAttrValues(collect);
            return spuItemGroupAttrVo;
        }).collect(Collectors.toList());

        return collect1;
    }


}