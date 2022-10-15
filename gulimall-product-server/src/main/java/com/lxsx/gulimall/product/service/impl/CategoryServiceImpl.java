package com.lxsx.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.constant.ProductConstant;
import com.lxsx.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.CategoryDao;
import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.CategoryService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redisson;

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

    // 递归查找所有菜单的子菜单
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

    /**
     * 查询所有目录
     * @return
     */
    public  Map<String,List<Catelog2Vo>> getCatelogsTreeData(){
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        String getCatelogsTree = opsForValue.get("getCatelogsTreeJSON");
        if (!StringUtils.isEmpty(getCatelogsTree)) {

            Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(getCatelogsTree,
                    new TypeReference<Map<String, List<Catelog2Vo>>>() {});

            return stringListMap;
        }
        long start = System.currentTimeMillis();
        //查询出全部 查询一次
        List<CategoryEntity> categoryEntities = this.list();
        long end = System.currentTimeMillis();
        System.out.println(("花费："+(end-start)));

        Map<String, List<Catelog2Vo>> catalogTree = categoryEntities.stream().filter(categoryEntity -> { //过滤出1级目录

            return categoryEntity.getCatLevel() == ProductConstant.SpuInfoEnum.CATELOG_LEVE_1.getType();

        }).collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {//映射成 前端需要的json形式的数据 key ：1级目录id vale 是二级目录集合
            //二级目录
            List<Catelog2Vo> catalogLeve2s = categoryEntities.stream().filter(categoryEntity -> { //过滤出2级目录
                return value.getCatId().longValue() == categoryEntity.getParentCid().longValue();
            }).map(categoryEntity -> {
                //三级目录
                List<Catelog2Vo.Catelog3Vo> catalogLeve3s = categoryEntities.stream().filter(categoryEntity1 -> {//过滤出3级目录
                    return categoryEntity.getCatId() == categoryEntity1.getParentCid();
                }).map(categoryEntity1 -> {
                    Catelog2Vo.Catelog3Vo catalog3Vo =
                            new Catelog2Vo.Catelog3Vo(categoryEntity.getCatId().toString(), categoryEntity1.getCatId().toString(), categoryEntity1.getName());
                    return catalog3Vo;
                }).collect(Collectors.toList());

                Catelog2Vo catalog2Vo =
                        new Catelog2Vo(value.getCatId().toString(), catalogLeve3s, categoryEntity.getCatId().toString(), categoryEntity.getName());
                return catalog2Vo;
            }).collect(Collectors.toList());

            return catalogLeve2s;
        }));

        String getCatelogsTreeJSON = JSON.toJSONString(catalogTree);
        opsForValue.set("getCatelogsTreeJSON", getCatelogsTreeJSON);
        return catalogTree;
    }

    //使用redis原生的分布式锁 解决并发问题
    public  Map<String,List<Catelog2Vo>> getCatelogsTreeByRedisLock(){
        //占用redis的分布式锁
        String token = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("getCatelogsTree-lock", token,30, TimeUnit.SECONDS);
        Map<String, List<Catelog2Vo>> catelogsTreeData;
        //加锁成功
        if (lock) {
            try{
                catelogsTreeData = getCatelogsTreeData();
            }finally {
                //删除锁
                String redisScript ="if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                String getlock = stringRedisTemplate.opsForValue().get("getCatelogsTree-lock");
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(redisScript,Long.class), Arrays.asList("getCatelogsTree-lock"),getlock);
            }
            return catelogsTreeData;
        }
        //加锁失败；；重试设加锁
        return getCatelogsTreeByRedisLock();
    };

    /**
     * 使用redisson 分布式解决方案
     * @return
     */
    public  Map<String,List<Catelog2Vo>> getCatelogsTreeByRedissonLock(){
        //占用redis的分布式锁
        RLock lock = redisson.getLock("getCatelogsTree-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> catelogsTreeData;
        //加锁成功
            try{
                catelogsTreeData = getCatelogsTreeData();
            }finally {
                //删除锁
               lock.unlock();
            }
            return catelogsTreeData;
    }


    //正确的使用缓存
    /**1.空结果缓存标志位：解决缓存穿透
     * 2.设置过期时间（随机值）：解决缓存雪崩
     * 3. 加锁：解决缓存击穿
     */
    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public  Map<String,List<Catelog2Vo>> getCatelogsTree() {

            long start = System.currentTimeMillis();
            //查询出全部 查询一次
            List<CategoryEntity> categoryEntities = this.list();
            long end = System.currentTimeMillis();
            System.out.println(("花费："+(end-start)));

            Map<String, List<Catelog2Vo>> catalogTree = categoryEntities.stream().filter(categoryEntity -> { //过滤出1级目录

                return categoryEntity.getCatLevel() == ProductConstant.SpuInfoEnum.CATELOG_LEVE_1.getType();

            }).collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {//映射成 前端需要的json形式的数据 key ：1级目录id vale 是二级目录集合
                //二级目录
                List<Catelog2Vo> catalogLeve2s = categoryEntities.stream().filter(categoryEntity -> { //过滤出2级目录
                    return value.getCatId().longValue() == categoryEntity.getParentCid().longValue();
                }).map(categoryEntity -> {
                    //三级目录
                    List<Catelog2Vo.Catelog3Vo> catalogLeve3s = categoryEntities.stream().filter(categoryEntity1 -> {//过滤出3级目录
                        return categoryEntity.getCatId() == categoryEntity1.getParentCid();
                    }).map(categoryEntity1 -> {
                        Catelog2Vo.Catelog3Vo catalog3Vo =
                                new Catelog2Vo.Catelog3Vo(categoryEntity.getCatId().toString(), categoryEntity1.getCatId().toString(), categoryEntity1.getName());
                        return catalog3Vo;
                    }).collect(Collectors.toList());

                    Catelog2Vo catalog2Vo =
                            new Catelog2Vo(value.getCatId().toString(), catalogLeve3s, categoryEntity.getCatId().toString(), categoryEntity.getName());
                    return catalog2Vo;
                }).collect(Collectors.toList());

                return catalogLeve2s;
            }));
            return catalogTree;
    }


    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public List<CategoryEntity> queryLeve1Catalog() {

        List<CategoryEntity> categoryEntities = this.list(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getCatLevel, ProductConstant.SpuInfoEnum.CATELOG_LEVE_1.getType()));
        return categoryEntities;
    }

    private List<CategoryEntity> nextLeveCatelog(CategoryEntity root, List<CategoryEntity> categoryEntities) {

        List<CategoryEntity> collect = categoryEntities.stream().filter(categoryEntity -> {
            return root.getCatId().longValue() == categoryEntity.getParentCid().longValue();
        }).map(categoryEntity -> { // 判断条件 相当于 集合为null
            categoryEntity.setChildren(nextLeveCatelog(categoryEntity,categoryEntities));
            return categoryEntity;
        }).collect(Collectors.toList());

        return collect;
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