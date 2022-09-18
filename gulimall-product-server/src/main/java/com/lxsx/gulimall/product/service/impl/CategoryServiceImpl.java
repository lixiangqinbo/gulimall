package com.lxsx.gulimall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.CategoryDao;
import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.CategoryService;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }
    //查询带树形的目录
    @Override
    public List<CategoryEntity> listWithTree() {

        // 1 查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2 组装成父子的树形结构 //java8新特性 补课 断言机制
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid().longValue() == 0
        ).map((menu)->{
            menu.setChildren(getChildrens(menu,entities));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort() == null?0:menu1.getSort()) - (menu2.getSort() == null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    // 递归查找所有菜单的子菜单 TODO 可以优化为循环
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().longValue() == root.getCatId().longValue();  // 注意此处应该用longValue()来比较，否则会出先bug，因为parentCid和catId是long类型
        }).map(categoryEntity -> {
            // 1 找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            // 2 菜单的排序
            return (menu1.getSort() == null?0:menu1.getSort()) - (menu2.getSort() == null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return children;

    }

    @Override
    public boolean saveCategory(CategoryEntity category) {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        for (CategoryEntity categoryEntity : categoryEntities) {
            if (categoryEntity.getName().equals(category.getName())) {
                return false;
            }
        }
        categoryDao.save(category);

        return true;
    }

    @Override
    public String getCatelogById(Long catelogId) {
        String name = categoryDao.getCatelogById(catelogId).getName();
        return name;
    }


    //查找出回显示目录的34--45--224路径
    @Override
    public Long[] getCatelogPath(Long categoryId) {
        List<Long> path = new ArrayList<>();
        ParentCid(categoryId,path);
        Collections.reverse(path);
        return (Long[]) path.toArray(new Long[path.size()]);
    }



    //递归去查找上个父节点
    public  List<Long>  ParentCid(Long categoryId, List<Long> path){
        path.add(categoryId);
        Long parentCid = categoryDao.getParentCid(categoryId);
        if ( parentCid!=null) {
            ParentCid(parentCid,path);
        }
        return path;
    }


}