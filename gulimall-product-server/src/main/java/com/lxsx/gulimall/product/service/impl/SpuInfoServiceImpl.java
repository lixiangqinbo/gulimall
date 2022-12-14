package com.lxsx.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.constant.ProductConstant;
import com.lxsx.gulimall.product.dao.AttrDao;
import com.lxsx.gulimall.product.entity.*;
import com.lxsx.gulimall.product.feign.CouponFeignService;
import com.lxsx.gulimall.product.feign.SpuUpFeigbService;
import com.lxsx.gulimall.product.feign.WareSkuFeignService;
import com.lxsx.gulimall.product.service.*;
import com.lxsx.gulimall.product.to.SpuInfoTo;
import com.lxsx.gulimall.product.vo.productvo.*;
import com.lxsx.gulimall.to.SkuReductionTo;
import com.lxsx.gulimall.to.SkuWareTo;
import com.lxsx.gulimall.to.SpuBoundsTo;
import com.lxsx.gulimall.to.es.SkuEsModel;
import com.lxsx.gulimall.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private SpuImagesService spuImagesService;

    @Resource
    private AttrService attrService;

    @Resource
    private SkuInfoService skuInfoService;
    @Resource
    private  SkuImagesService skuImagesService;
    @Resource
    private ProductAttrValueService productAttrValueService;
    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    private CouponFeignService couponFeignService;
    @Resource
    private WareSkuFeignService wareSkuFeignService;
    @Resource
    private  BrandService brandService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private SpuUpFeigbService spuUpFeigbService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<SpuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        //'0' ??????????????????
        Object catobj = params.get("catelogId");
        if (catobj!=null) {
            Long catelogId =  Long.valueOf(params.get("catelogId").toString());

                queryWrapper.eq(catelogId!=0,SpuInfoEntity::getCatalogId,catelogId);

        }
        if (params.get("status")!=null&&StringUtils.isNotBlank((String) params.get("status"))) {
            Integer status = Integer.parseInt(params.get("status").toString()) ;
                queryWrapper.eq(SpuInfoEntity::getPublishStatus,status);
        }
        //'0' ??????????????????
        if (params.get("brandId")!=null) {
            Long brandId = Long.valueOf(params.get("brandId").toString());
                queryWrapper.eq(brandId!=0,SpuInfoEntity::getBrandId,brandId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
/*            queryWrapper
            .like(SpuInfoEntity::getSpuName,key)
                    .like(SpuInfoEntity::getSpuDescription,key)
                    .like(SpuInfoEntity::getCreateTime,key)
                    .like(SpuInfoEntity::getUpdateTime,key)
                    .like(SpuInfoEntity::getWeight,key);*/
            queryWrapper.and(wrapper ->{
                wrapper.like(SpuInfoEntity::getSpuName,key).or()
                    .like(SpuInfoEntity::getSpuDescription,key).or()
                    .like(SpuInfoEntity::getCreateTime,key).or()
                    .like(SpuInfoEntity::getUpdateTime,key).or()
                    .like(SpuInfoEntity::getWeight,key);
            });
        }

        String order = (String) params.get("order");
        if (StringUtils.isNotBlank(order)) {
            queryWrapper.orderBy(StringUtils.isNotBlank(order), "asc".equals(order), SpuInfoEntity::getId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),queryWrapper);

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo vo) {
        /**
         * ?????????
         * 1.??????spu??????????????? pms_spu_info && ??????spu???????????????pms_spu_info_desc,spu????????????pms_spu_image
         * 2.??????spu??????????????? pms_product_attr_value
         * 3?????????spu??????????????????gulimall_sms->sms_spu_bounds
         * 4.????????????spu???????????????sku??????
         */
        // 1 .??????spu??????????????? pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);

        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        //??????spu???????????????pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        List<String> decript = vo.getDecript();
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //??????spu????????????pms_spu_image
        SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
        String imagesUrl = String.join(",",vo.getImages());
        spuImagesEntity.setId(spuId);//TODO
        spuImagesEntity.setSpuId(spuId);//TODO
        spuImagesEntity.setImgUrl(imagesUrl);
        spuImagesService.saveSpuImage(spuImagesEntity);

        //2.??????spu??????????????? pms_product_attr_value
        List<ProductAttrValueEntity> productAttrValueEntities = vo.getBaseAttrs().stream().map(baseAttrs -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(baseAttrs.getAttrId());
            attrValueEntity.setQuickShow(baseAttrs.getShowDesc());
            attrValueEntity.setAttrValue(baseAttrs.getAttrValues());
            AttrEntity attrEntity = attrService.getById(baseAttrs.getAttrId());
            attrValueEntity.setAttrName(attrEntity.getAttrName());
            attrValueEntity.setSpuId(spuId);
            return attrValueEntity;
        }).collect(Collectors.toList());

        productAttrValueService.saveProductAttrValues(productAttrValueEntities);

        //3?????????spu??????????????????gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        if (bounds!=null) {
            SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
            spuBoundsTo.setSpuId(spuId);
            spuBoundsTo.setBuyBounds(bounds.getBuyBounds());
            spuBoundsTo.setGrowBounds(bounds.getGrowBounds());
            R res = couponFeignService.saveSpuBounds(spuBoundsTo);
            if (res.getCode() != R.ok().getCode()) {
                log.error("?????????????????????????????????");
            }
        }
        //4.????????????spu???????????????sku??????
        List<Skus> skus = vo.getSkus();

        if (skus!=null&&skus.size()>0) {
            skus.forEach(sku ->{
                //?????????????????????
                String defaultImg = "";
                List<Images> imagesList = sku.getImages();
                for (Images images : imagesList) {
                    if (images.getDefaultImg()==1){
                         defaultImg = images.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                // ??????sku??????????????? pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                if (imagesList!=null&&imagesList.size()>0) {
                    //??????sku??????????????? pms_sku_images
                    List<SkuImagesEntity> skuImagesEntities = imagesList.stream().map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setDefaultImg(image.getDefaultImg());
                        skuImagesEntity.setImgUrl(image.getImgUrl());
                        return skuImagesEntity;

                    }).filter(skuImagesEntity -> { //????????????????????????????????????
                        return StringUtils.isNotBlank(skuImagesEntity.getImgUrl());
                    }).collect(Collectors.toList());

                    skuImagesService.saveSkuImages(skuImagesEntities);
                }


                //??????sku????????????????????? pms_sku_sale_attr_value
                List<Attr> attrList = sku.getAttr();
                if (attrList!=null&&attrList.size()>0) {

                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrList.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        skuSaleAttrValueEntity.setAttrName(attr.getAttrName());
                        skuSaleAttrValueEntity.setAttrValue(attr.getAttrValue());
                        skuSaleAttrValueEntity.setAttrId(attr.getAttrId());
                        skuSaleAttrValueEntity.setSkuId(skuId);
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    skuSaleAttrValueService.saveSkuSaleAttrValue(skuSaleAttrValueEntities);
                }


                //??????sku??????????????????????????? gulimall_sms -> sms_sku_ladder | sms_sku_full...
                SkuReductionTo skuReductionTo =new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1) {
                    R res = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (res.getCode() != R.ok().getCode()) {
                        log.error("?????????????????????????????????");
                    }
                }


            });
        }

    }

    /**
     * ??????????????????
     * @param spuId ??????ID
     */
    @Override
    public void spuUp(Long spuId) {
        //???????????????????????????
        SpuInfoEntity spuInfoEntity = this.getById(spuId);
        if (spuInfoEntity.getPublishStatus() == ProductConstant.SpuInfoEnum.PUBLISH_STATUS_UP.getType()) {
            log.error("???????????????????????????");
            return;
        }


        //?????? Es ?????????
        //?????????spu???????????????(???????????????????????? ??????sku???????????????)
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.querySpuAttrValueList(spuId);
        //????????? attr ??????????????????????????????
        List<ProductAttrValueEntity> collect = attrValueEntities.stream().filter(productAttrValueEntity -> {
            AttrEntity attrEntity = attrService.getById(productAttrValueEntity.getAttrId());
            return attrEntity.getSearchType() == ProductConstant.AttrEnum.SEARCH_TYPE_UP.getType();
        }).collect(Collectors.toList());

        List<SkuEsModel.Attrs> attrsList = collect.stream().map(productAttrValueEntity -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(productAttrValueEntity, attrs);
            return attrs;
        }).collect(Collectors.toList());

        //????????????????????????
        List<SkuInfoEntity> skuInfoEntities =
                skuInfoService.list(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));

        //????????????????????????????????????????????????
        List<Long> skuIds = skuInfoEntities.stream().map(skuInfoEntity -> {
            return skuInfoEntity.getSkuId();
        }).collect(Collectors.toList());

        Map<Long ,Boolean> hasStock = new HashMap<>();
        try {
//            R waresku = wareSkuFeignService.getWaresku(skuIds);
//            Object data = waresku.get("data");
//            List<Map<String,Object>> skuWareTos = (List<Map<String, Object>>) data;
//
//            skuWareTos.forEach(skuWareTo -> {
//                hasStock.put(Long.valueOf(skuWareTo.get("skuId").toString()),Boolean.valueOf(skuWareTo.get("hasStock").toString()));
//            });
            R res = wareSkuFeignService.getWaresku(skuIds);
            List<SkuWareTo> skuWareTos = res.getData(new TypeReference<List<SkuWareTo>>() {
            });
            // TODO java.lang.NullPointerException
            skuWareTos.forEach(skuWareTo -> {
                hasStock.put(skuWareTo.getSkuId(),skuWareTo.isHasStock());
            });
        }catch (Exception e){
                log.error("????????????????????????{}",e);
        }

        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(skuInfoEntity -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfoEntity, skuEsModel);
            //sku_price,sku_img
            skuEsModel.setSkuPrice(skuInfoEntity.getPrice());
            skuEsModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
            //hasStock ,hotScore
            if (hasStock==null) {
                skuEsModel.setHasStock(true);
            }else {
                skuEsModel.setHasStock(hasStock.get(skuInfoEntity.getSkuId())==null?false:hasStock.get(skuInfoEntity.getSkuId()));
            }

            skuEsModel.setHotScore(Long.valueOf(ProductConstant.SpuInfoEnum.DEFAULT_HOTSCORE.getType()));
            //??????????????????
            BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
            skuEsModel.setBrandId(brandEntity.getBrandId());
            skuEsModel.setBrandImg(brandEntity.getLogo());
            skuEsModel.setBrandName(brandEntity.getName());

            //????????????
            CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getCatalogId());
            skuEsModel.setCatalogId(categoryEntity.getCatId());
            skuEsModel.setCatalogName(categoryEntity.getName());

            skuEsModel.setAttrs(attrsList);
            return skuEsModel;

        }).collect(Collectors.toList());

        //??????Es
        R res = spuUpFeigbService.spuStatusUp(skuEsModels);
        if (res.getCode()== R.ok().getCode()) {
            //???????????????????????????
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setPublishStatus(ProductConstant.SpuInfoEnum.PUBLISH_STATUS_UP.getType());
            this.updateById(spuInfoEntity);
        }else{
            // TODO ??????????????????????????????????????????
        }
    }

    @Override
    public List<SpuInfoTo> getSpuinfoWithBrandName(List<Long> skuIds) {
        List<SpuInfoTo> spuInfoTos= this.baseMapper.selectSpuinfoWithBrandName(skuIds);
        return spuInfoTos;

    }

}