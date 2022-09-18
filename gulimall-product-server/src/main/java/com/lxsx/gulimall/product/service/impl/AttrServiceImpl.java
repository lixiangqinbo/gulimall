package com.lxsx.gulimall.product.service.impl;

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
import java.util.stream.Collectors;

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
                //所属分组名字
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

    @Override
    @Transactional
    public void cascadeSave(AttrEntityVo attrEntityVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrEntityVo,attrEntity);
        //保存pms_attr表
        attrDao.insert(attrEntity);
        //pms_attr_attrgroup_relation表

        //基本属性才更改关系表
        if (attrEntityVo.getAttrGroupId()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType()){
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
            //查询目录所在下所有分组
            List<AttrGroupEntity> attrGroupEntities =
                    attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", attrEntity.getCatelogId()));
            attrGroupDao.selectList(null);
            attrEntityVO.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            attrEntityVO.setAttrGroupEntities(attrGroupEntities);
        }



        return attrEntityVO;
    }

    @Override
    public void cascadeUpdateById(AttrEntityVo attrVo) {
        if (attrVo==null) {
            throw new RuntimeException("参数为null,不可用！");
        }
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        //更新属性单表信息
        attrDao.updateById(attrEntity);
        //如果是基本参数 还得更改相关表
        if (attrVo.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType()) {

            attrAttrgroupRelationDao.updateByAttrId(attrVo.getAttrId(),attrVo.getAttrGroupId());

        }else if(attrVo.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_SALE.getType()){//情况二：直接更改相关表ATTR_TYPE_BASE ---> ATTR_TYPE_SALE
            //检查是否是：ATTR_TYPE_BASE ---> ATTR_TYPE_SALE
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectByAttrId(attrVo.getAttrId());
            //关系表有记录，则删除
            if (attrAttrgroupRelationEntity!=null) {
                attrAttrgroupRelationDao.deleteById(attrAttrgroupRelationEntity.getId());
            }
        }
    }

        //多对多的关系
    @Override
    @Transactional
    public void cascadeRemove(List<Long> asList) {
        attrDao.deleteBatchIds(asList);
        attrAttrgroupRelationDao.deleteBatchByAttrId(asList);

    }

}