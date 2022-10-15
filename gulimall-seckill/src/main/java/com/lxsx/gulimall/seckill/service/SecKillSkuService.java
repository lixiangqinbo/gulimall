package com.lxsx.gulimall.seckill.service;

import com.lxsx.gulimall.exception.NotFindInfoException;
import com.lxsx.gulimall.exception.RpcException;
import com.lxsx.gulimall.exception.SecKillRuleException;
import com.lxsx.gulimall.seckill.vo.SeckillSessionEntityVo;
import com.lxsx.gulimall.seckill.vo.SeckillSkuRelationEntityVo;
import com.lxsx.gulimall.seckill.vo.SkuInfoEntityVo;

import java.util.List;

public interface SecKillSkuService {
     void updateSeckillSkuFuture3days() throws Exception;
     void saveSessionSkuInfo(List<SeckillSessionEntityVo> seckillSessionEntityVos,
                                    List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos,
                                    List<SkuInfoEntityVo> skuInfos);

     void saveSessionInfo(List<SeckillSessionEntityVo> seckillSessionEntityVos, List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos);

     List<SkuInfoEntityVo> getSkuInfos(List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos) throws RpcException, NotFindInfoException;
     List<SeckillSkuRelationEntityVo> getSeckillSkuRelationEntityVos(List<SeckillSessionEntityVo> seckillSessionEntityVos) throws RpcException, NotFindInfoException;
     List<SeckillSessionEntityVo> getSeckillSessionEntityVos() throws RpcException, NotFindInfoException;

    List<SeckillSkuRelationEntityVo> queryCurrentSeckillSkus() throws NotFindInfoException;

    SeckillSkuRelationEntityVo querySeckillSkuBySkuId(Long skuIds);

    SeckillSkuRelationEntityVo killSuk(String killId, String key, Integer num) throws NotFindInfoException, SecKillRuleException;
}
