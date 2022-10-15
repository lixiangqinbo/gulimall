package com.lxsx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lxsx.gulimall.constant.ProductConstant;
import com.lxsx.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.lxsx.gulimall.product.dao.AttrGroupDao;
import com.lxsx.gulimall.product.dao.CategoryDao;
import com.lxsx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.lxsx.gulimall.product.entity.AttrGroupEntity;
import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.CategoryService;
import com.lxsx.gulimall.product.vo.AttrEntityVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.AttrDao;
import com.lxsx.gulimall.product.entity.AttrEntity;
import com.lxsx.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrDao attrDao;
    @Resource
    private CategoryService categoryService;
    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Resource
    private  AttrGroupDao attrGroupDao;

    @Resource
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageForAttr(Map<String, Object> params) {
        //查询出 所属分类名字 和 所属分组名字
        IPage<AttrEntity> page =
                attrDao.queryAttrList(new Query<AttrEntity>().getPage(params), params);

        List<AttrEntity> attrEntities = page.getRecords();

        List<AttrEntityVo> attrEntitieVos = attrEntities.stream().map(attrEntity -> { //得到一个新的流
            //将AttrEntity 全部拷贝为attrEntityVo
            AttrEntityVo attrEntityVo = new AttrEntityVo();
            BeanUtils.copyProperties(attrEntity, attrEntityVo);
            //查询所属分类名字
            if (attrEntity.getCatelogId() != null) {
                CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
                if (categoryEntity!=null){
                    attrEntityVo.setCatelogName(categoryEntity.getName());
                }
            }
            //只有基本属性才需要再去查询 分组信息，销售属性没有分组信息
            if (attrEntity.getAttrId() != null && attrEntity.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType()) {
                //所属分组名字 : TODO 不清楚是不是多对多的关系
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectByAttrId(attrEntity.getAttrId());
                if (attrAttrgroupRelationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrEntityVo.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            return attrEntityVo;
        }).collect(Collectors.toList());
        //换掉里面的集合数据
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrEntitieVos);
        return pageUtils;
    }

//    @Override
//    public PageUtils queryPageForAttr1(Map<String, Object> params) {
//        //查询出 所属分类名字 和 所属分组名字
//        IPage<AttrEntity> page =
//                attrDao.queryAttrList(new Query<AttrEntity>().getPage(params), params);
//
//        List<AttrEntity> attrEntities = page.getRecords();
//
//        List<AttrAttrgroupRelationEntity> collect = attrEntities.stream().map(attrEntity -> { //得到一个新的流
//            //将AttrEntity 全部拷贝为attrEntityVo
//            AttrEntityVo attrEntityVo = new AttrEntityVo();
//            BeanUtils.copyProperties(attrEntity, attrEntityVo);
//            //查询所属分类名字
//            if (attrEntity.getCatelogId() != null) {
//                CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
//                if (categoryEntity != null) {
//                    attrEntityVo.setCatelogName(categoryEntity.getName());
//                }
//            }
//            //只有基本属性才需要再去查询 分组信息，销售属性没有分组信息
//            if (attrEntity.getAttrId() != null && attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType()) {
//                //所属分组名字 : TODO 多对多的情况
//                List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectByAttrId1(attrEntity.getAttrId());
//                if (attrAttrgroupRelationEntities != null || attrAttrgroupRelationEntities.size() != 0) {
//                    attrEntityVo.setAttrAttrgroupRelationEntities(attrAttrgroupRelationEntities);
//                }
//            }
//            return attrEntityVo;
//        }).flatMap(attrEntityVo -> {
//            return attrEntityVo.getAttrAttrgroupRelationEntities().stream();
//        }).collect(Collectors.toList());
//
//        //换掉里面的集合数据
//        PageUtils pageUtils = new PageUtils(page);
//        pageUtils.setList(collect);
//        return pageUtils;
//    }

    @Override
    @Transactional
    public void cascadeSave(AttrEntityVo attrEntityVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrEntityVo,attrEntity);
        //保存pms_attr表
        attrDao.insert(attrEntity);
        //pms_attr_attrgroup_relation表

        //基本属性才更改关系表
        if (attrEntityVo.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType()){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity
                    =new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrEntityVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public AttrEntityVo getDetail(Long attrId) {
        AttrEntity attrEntity = attrDao.selectById(attrId);

        AttrEntityVo attrEntityVO = new AttrEntityVo();
        BeanUtils.copyProperties(attrEntity,attrEntityVO);

        Long[] catelogPath = categoryService.getCatelogPath(attrEntity.getCatelogId());
        attrEntityVO.setCatelogPath(catelogPath);
        //基本属性 才需要携带以下信息attrGroupId
        if (attrEntity.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType()) {
            //查询出对应的分组
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectByAttrId(attrEntity.getAttrId());
            if (attrAttrgroupRelationEntity!=null) { //如果为null说明 关系已被删除
                attrEntityVO.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            }
            //查询目录所在下所有分组
            List<AttrGroupEntity> attrGroupEntities =
                    attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", attrEntity.getCatelogId()));
            attrGroupDao.selectList(null);
            attrEntityVO.setAttrGroupEntities(attrGroupEntities);
        }



        return attrEntityVO;
    }

    @Override
    @Transactional
    public void cascadeUpdateById(AttrEntityVo attrVo) {
        if (attrVo==null) {
            throw new RuntimeException("参数为null,不可用！");
        }
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        //更新属性单表信息
        attrDao.updateById(attrEntity);

        //判断关系表中是否已经有关系映射存在 ，如果是基本参数 还得更改相关表
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                attrAttrgroupRelationDao.selectByAttrId(attrVo.getAttrId());

        //如果是基本参数 还得更改相关表
        if (attrVo.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType()) {
            //基本参数 && 关系表已经有映射关系
            if (attrAttrgroupRelationEntity!=null) {
                //基本属性有关系表，直接更新
                attrAttrgroupRelationDao.updateByAttrId(attrVo.getAttrId(),attrVo.getAttrGroupId());
            }else { ////基本参数 && 关系表无有映射关系
                //不存在，就新增关系表
                AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
                relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
                relationEntity.setAttrId(attrVo.getAttrId());
                attrAttrgroupRelationDao.insert(relationEntity);
            }

            //销售属性无关系表
        }else if(attrVo.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_SALE.getType()){
            /**
             * 多情况：
             * 1.ATTR_TYPE_BASE 属性改为 ATTR_TYPE_SALE：删除关系表并更新，属性表数据
             * 2.ATTR_TYPE_SALE 提交属性，直接更改
             */

            //1.ATTR_TYPE_BASE 属性改为 ATTR_TYPE_SALE：删除关系表并更新，属性表数据
            if (attrAttrgroupRelationEntity!=null) {
                attrAttrgroupRelationDao.deleteById(attrAttrgroupRelationEntity.getId());
            }
            //2.ATTR_TYPE_SALE 提交属性，直接更改(149行已经更新)

        }
    }

        //多对多的关系
    @Override
    @Transactional
    public void cascadeRemove(List<Long> asList) {
        attrDao.deleteBatchIds(asList);
        attrAttrgroupRelationDao.deleteBatchByAttrId(asList);

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {

        // attr_type 构造条件
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<AttrEntity>()
                .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getTypeName().equalsIgnoreCase(type) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getType());

        // 是否查询条件
        if(catelogId != 0){
            queryWrapper.eq(AttrEntity::getCatelogId,catelogId);
        }
        // 模糊查询？
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            //attr_id  attr_name
            queryWrapper.and((wrapper)->{
                wrapper.eq(AttrEntity::getAttrId,key).or().like(AttrEntity::getAttrName,key);
            });
        }

        IPage<AttrEntity> page = attrDao.selectPage(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrEntityVo> respVos = records.stream().map((attrEntity) -> {
            AttrEntityVo attrRespVo = new AttrEntityVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //1、设置分类和分组的名字
            if( ProductConstant.AttrEnum.ATTR_TYPE_BASE.getTypeName().equalsIgnoreCase(type)){
                AttrAttrgroupRelationEntity attrId = attrAttrgroupRelationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
                if (attrId != null && attrId.getAttrGroupId()!=null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrRespVo.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }



}