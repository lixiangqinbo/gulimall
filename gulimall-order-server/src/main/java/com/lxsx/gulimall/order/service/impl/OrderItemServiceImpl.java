package com.lxsx.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.order.constants.RpcStatusCode;
import com.lxsx.gulimall.order.web.feign.SkuInfoFeignService;
import com.lxsx.gulimall.order.web.to.SpuInfoTo;
import com.lxsx.gulimall.order.web.vo.CartItemVo;
import com.lxsx.gulimall.utils.Query;
import com.lxsx.gulimall.utils.R;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;



import com.lxsx.gulimall.order.dao.OrderItemDao;
import com.lxsx.gulimall.order.entity.OrderItemEntity;
import com.lxsx.gulimall.order.service.OrderItemService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Resource
    private SkuInfoFeignService skuInfoFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveItems(List<CartItemVo> cartItemVos, String orderSn) {
        //获取skuid 对应的目录 和品牌 spu等信息
        Map<Long, SpuInfoTo> spuInfoToMap =getSpuinfo (cartItemVos);
        //订单id 订单号
        List<OrderItemEntity> orderItemEntities = cartItemVos.stream().map(cartItemVo -> {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderSn(orderSn);
            String attr = StringUtils.collectionToDelimitedString(cartItemVo.getSkuAttrValues(), ";");
            orderItemEntity.setSkuAttrsVals(attr);
            orderItemEntity.setSkuId(cartItemVo.getSkuId());
            orderItemEntity.setSkuPrice(cartItemVo.getPrice());
            orderItemEntity.setSkuName(cartItemVo.getTitle());
            orderItemEntity.setSkuPic(cartItemVo.getImage());
            orderItemEntity.setSkuQuantity(cartItemVo.getCount());
            int total = cartItemVo.getPrice().intValue() * cartItemVo.getCount();
            orderItemEntity.setGiftGrowth(total);
            orderItemEntity.setGiftIntegration(total);
            orderItemEntity.setIntegrationAmount(cartItemVo.getTotalPrice());
            if (spuInfoToMap!=null) {
                SpuInfoTo spuInfoTo = spuInfoToMap.get(cartItemVo.getSkuId());
                orderItemEntity.setSpuBrand(spuInfoTo.getBrandName());
                orderItemEntity.setCategoryId(spuInfoTo.getCatalogId());
                orderItemEntity.setSpuName(spuInfoTo.getSpuName());
                orderItemEntity.setSpuId(spuInfoTo.getSpuId());
            }
            //品牌和目录信息 RPC
            return orderItemEntity;
        }).collect(Collectors.toList());
        this.saveBatch(orderItemEntities);

    }

    @Override
    public List<OrderItemEntity> queryOrderItems(String orderSn) {
        List<OrderItemEntity> orderItemEntities = this.baseMapper.selectList(new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderSn, orderSn));
        return orderItemEntities;
    }

    /**
     * 获取skuid 对应的目录 和品牌 spu等信息
     * @param cartItemVos
     * @return
     */
    @Override
    public Map<Long, SpuInfoTo> getSpuinfo (List<CartItemVo> cartItemVos){
        List<Long> collect = cartItemVos.stream().map(cartItemVo -> {
            return cartItemVo.getSkuId();
        }).collect(Collectors.toList());
        R res = skuInfoFeignService.getSpuinfoWithBrandName(collect);
        if (res.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
            List<SpuInfoTo> data = res.getData(new TypeReference<List<SpuInfoTo>>() {
            });
            Map<Long, SpuInfoTo> collect1 = data.stream().collect(Collectors.toMap(SpuInfoTo::getSkuId, spuInfoTo -> {
                return spuInfoTo;
            }));
            return collect1;
        }
         return null;
    }

    @Override
    public SpuInfoTo getSpuinfoBySkuId(Long skuId) {
        SpuInfoTo spuInfoTo = new SpuInfoTo();
        List<Long> skuIds = Arrays.asList(skuId);
        R res = skuInfoFeignService.getSpuinfoWithBrandName(skuIds);
        if (res.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
            List<SpuInfoTo> data = res.getData(new TypeReference<List<SpuInfoTo>>() {
            });

            Map<Long, SpuInfoTo> collect1 = data.stream().collect(Collectors.toMap(SpuInfoTo::getSkuId, spuInfoTo1 -> {
                return spuInfoTo1;
            }));
            spuInfoTo = collect1.get(skuId);
        }
        return spuInfoTo;
    }

}