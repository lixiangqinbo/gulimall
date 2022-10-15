package com.lxsx.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.product.entity.SkuImagesEntity;
import com.lxsx.gulimall.product.entity.SpuInfoDescEntity;
import com.lxsx.gulimall.product.service.*;
import com.lxsx.gulimall.product.to.SkuPriceTo;
import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemSaleAttrVo;
import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemVo;
import com.lxsx.gulimall.product.vo.skuinfovo.SpuItemGroupAttrVo;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.web.feign.SecKillFeignService;
import com.lxsx.gulimall.web.vo.SeckillInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.SkuInfoDao;
import com.lxsx.gulimall.product.entity.SkuInfoEntity;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Resource
    private SkuImagesService skuImagesService;
    @Resource
    private SpuInfoDescService spuInfoDescService;
    @Resource
    private AttrGroupService attrGroupService;
    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    private SecKillFeignService secKillFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        //AND catelogId = ?
        Object catobj = params.get("catelogId");
        if (catobj!=null) {
            Long catelogId =  Long.valueOf(params.get("catelogId").toString());
            queryWrapper.eq(SkuInfoEntity::getCatalogId,catelogId);
        }
        //AND brandId =?
        if (params.get("brandId")!=null) {
            Long brandId = Long.valueOf(params.get("brandId").toString());
            queryWrapper.eq(SkuInfoEntity::getBrandId,brandId);
        }
        //AND (sku_name like ? or sku_subtitle like ? or sku_title like ?)
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(wrapper ->{
                wrapper.like(SkuInfoEntity::getSkuName,key).or()
                        .like(SkuInfoEntity::getSkuSubtitle,key).or()
                        .like(SkuInfoEntity::getSkuTitle,key).or();
            });
        }
        // AND sku_prices be
        Object min = params.get("min");
        Object max = params.get("max");
        boolean resMax =false;
        boolean resMin =false;
        if (min!=null && max!=null) {
            resMax = new BigDecimal((String) max).compareTo(new BigDecimal(0)) != 0;//是否为"0"
            resMin = new BigDecimal((String) min).compareTo(new BigDecimal(0)) != 0;//是否为"0"
        }
        if (resMin && !resMax) {//min 传的非‘0’ max为 ‘0’
            //AND pku_price > min
            queryWrapper.ge(SkuInfoEntity::getPrice,new BigDecimal((String)min));
        }
        if (!resMin && resMax) {//min 传的‘0’ max为 非‘0’
            //AND pku_price < max
            queryWrapper.le(SkuInfoEntity::getPrice,new BigDecimal((String)max));
        }

        if (resMin && resMax) { //都不是'0'
            queryWrapper.between(SkuInfoEntity::getPrice,new BigDecimal(min.toString()),new BigDecimal(max.toString()));
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    /**
     * 查询关于sku的所有信息
     * 基础属性啊，销售属性啊，基础信息，图片信息...
     * pms_sku_info,
     * pms_sku_images,
     *
     * @param skuId sku的Id
     * @return
     */
    @Override
    public SkuItemVo querySkuAllinfo(Long skuId) throws ExecutionException, InterruptedException{
        SkuItemVo skuItemVo = new SkuItemVo();
        //TODO 补查询是都有库存
        /**
         * 异步的方式：获取sku的基本信息
         * 无需依赖其他线程 就单独异步开始。但是需要提供返回值，以便后续依赖线程的使用
         */
        CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = this.baseMapper.selectById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);
        /**
         * 异步获取改sku的图片信息
         * 无需依赖其他线程 就单独异步开始
         */
        CompletableFuture<Void> queryImagesBySkuIdFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.queryImagesBySkuId(skuId);
            skuItemVo.setImages(skuImagesEntities);
        }, executor);
        /**
         * 获取SPU的介绍
         * 依赖上面的直接结果，执行完后在异步并行执行
         */
        CompletableFuture<Void> querySpuInfoDescBySpuIdFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.querySpuInfoDescBySpuId(res.getSpuId());
            skuItemVo.setDesc(spuInfoDescEntity);
        }, executor);

        /**
         * 规格属性（spu下共用的）
         * 依赖上面的直接结果，执行完后在异步并行执行
         */
        CompletableFuture<Void> spuItemGroupAttrFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(res -> {
            List<SpuItemGroupAttrVo> spuItemGroupAttrVos = attrGroupService.queryAttrGropWithAttrsByCatId(res.getCatalogId(), res.getSpuId());
            skuItemVo.setGroupAttrs(spuItemGroupAttrVos);
        }, executor);

        /**
         * 销售属性
         * 依赖上面的直接结果，执行完后在异步并行执行
         */
        CompletableFuture<Void> skuItemSaleAttrFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(res -> {
            List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.querySkuSaleValueBySkuId(res.getSpuId());
            skuItemVo.setSaleAttr(skuItemSaleAttrVos);
        }, executor);

        /**
         * 查村当前的sku是都又相关的秒杀信息
         */
        CompletableFuture<Void> exceptionally = CompletableFuture.runAsync(() -> {
            R seckillSku = secKillFeignService.getSeckillSku(skuId);
            SeckillInfoVo seckillInfoVo = seckillSku.getData(new TypeReference<SeckillInfoVo>() {
            });
            skuItemVo.setSeckillSkuVo(seckillInfoVo);

        }, executor).exceptionally((exception) -> {
            String message = exception.getMessage();
            log.error(message);
            return null;
        });

        /**
         * 等待所有所有的任务完成
         */
        CompletableFuture.allOf(queryImagesBySkuIdFuture,querySpuInfoDescBySpuIdFuture,spuItemGroupAttrFuture,skuItemSaleAttrFuture,exceptionally).get();

        return skuItemVo;
    }

    /**
     * 更具skuids查询出价格
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuPriceTo> querySkuPriceByskuIds(List<Long> skuIds) {
        List<SkuPriceTo> priceTos = this.baseMapper.selectSkuPriceToList(skuIds);
        return priceTos;
    }

    @Override
    public List<SkuInfoEntity> querySkuInfoBySkuIds(List<Long> skuIds) {
        List<SkuInfoEntity> skuInfoEntities=this.baseMapper.selectSkuInfoBySkuIdsskuIds(skuIds);
        return skuInfoEntities;
    }

}