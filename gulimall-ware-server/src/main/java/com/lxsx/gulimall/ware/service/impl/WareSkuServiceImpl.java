package com.lxsx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lxsx.gulimall.constant.WareConstant;
import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.mq.StockDetailTo;
import com.lxsx.gulimall.mq.StockLockedTo;
import com.lxsx.gulimall.to.LockWareSkuTo;
import com.lxsx.gulimall.to.SkuInfoTo;
import com.lxsx.gulimall.to.SkuWareTo;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.ware.entity.*;
import com.lxsx.gulimall.ware.enumm.StockDetailEnum;
import com.lxsx.gulimall.ware.exception.WareLockedException;
import com.lxsx.gulimall.ware.feign.SkuInfoFeignService;
import com.lxsx.gulimall.ware.service.PurchaseDetailService;
import com.lxsx.gulimall.ware.service.WareOrderTaskDetailService;
import com.lxsx.gulimall.ware.service.WareOrderTaskService;
import com.lxsx.gulimall.ware.vo.CartItemVo;
import com.lxsx.gulimall.ware.vo.PurchaseDetailsVo;
import com.lxsx.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.ware.dao.WareSkuDao;
import com.lxsx.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private PurchaseDetailService purchaseDetailService;
    @Resource
    private SkuInfoFeignService skuInfoFeignService;
    @Resource
    private WareSkuDao wareSkuDao;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private WareOrderTaskService wareOrderTaskService;
    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {


        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper();
        String skuId = (String)params.get("skuId");
        String wareId =(String)params.get("wareId");
        // AND sku_id= ? AND ware_id = ?
        wrapper.eq(StringUtils.isNotBlank(skuId),WareSkuEntity::getSkuId,skuId);
        wrapper.eq(StringUtils.isNotBlank(wareId),WareSkuEntity::getWareId,wareId);


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(List<PurchaseDetailsVo> collect) {
        collect.forEach(item ->{
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(item.getItemId());
            if (purchaseDetailEntity!=null) {
                //看仓库里面是否有这个记录
                WareSkuEntity wareSkuEntity = this.getOne(new LambdaQueryWrapper<WareSkuEntity>()
                        .eq(WareSkuEntity::getSkuId, purchaseDetailEntity.getSkuId())
                        .eq(WareSkuEntity::getWareId,purchaseDetailEntity.getWareId())
                );
                //有就直接累加库存
                if (wareSkuEntity!=null) {
                    Integer stockNum = wareSkuEntity.getStock();
                    Integer skuNum = purchaseDetailEntity.getSkuNum();
                    wareSkuEntity.setStock(stockNum+skuNum);
                    this.updateById(wareSkuEntity);

                }else{ //没有就新增
                    SkuInfoTo skuInfoTo = new SkuInfoTo();
                    skuInfoTo.setSkuId(purchaseDetailEntity.getSkuId());
                    WareSkuEntity  wSku= new WareSkuEntity();
                    try { //出现异常 继续下执行，保证事务统一，TODO 后续修改为分布是事务 seata 处理
                        R r = skuInfoFeignService.querySkuInfoEntityById(skuInfoTo);
                        if (r.getCode()==0) {
                            Map<String,Object> data = (Map<String, Object>) r.get("data");
                            wSku.setSkuName((String) data.get("skuName"));
                        }
                    }catch (Exception e){

                    }

                    wSku.setStock(purchaseDetailEntity.getSkuNum());
                    wSku.setSkuId(purchaseDetailEntity.getSkuId());
                    wSku.setWareId(purchaseDetailEntity.getWareId());
                    wSku.setStock(WareConstant.WareSkuEnum.DEFAULT_STOCK_LOCKED.getType());

                    this.save(wSku);
                }
            }

        });
    }

    @Override
    public List<SkuWareTo> getWareskuByskuId(List<Long> skuIds) {

        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectWareSkuListByskuIds(skuIds);
        List<SkuWareTo> skuWareTos = null;

        try{
            skuWareTos = wareSkuEntities.stream().map(wareSkuEntity -> {
             SkuWareTo skuWareTo = new SkuWareTo();
             skuWareTo.setSkuId(wareSkuEntity.getSkuId());
             skuWareTo.setStock(wareSkuEntity.getStock());
             skuWareTo.setHasStock(wareSkuEntity.getStock() > 0 ? true : false);
             return skuWareTo;
         }).collect(Collectors.toList());
     }
     catch (NullPointerException e){
            log.error("空指针了！");
     }


        return skuWareTos;
    }

    /**
     * 锁库存
     * @param lockWareSkuTo
     * @return
     */
    @Override
    public List<LockWareSkuTo> lockWareskuByskuId(List<LockWareSkuTo> lockWareSkuTo) {

        /**
         * 保存库存工作单
         * 记录后续人工干预
         */
        for (LockWareSkuTo wareSkuTo : lockWareSkuTo) {
            //受影响条数
            Integer integer = this.baseMapper.lockWaresku(wareSkuTo.getSkuId(), wareSkuTo.getWareId(), wareSkuTo.getNum());
            if (integer!=0) { //锁定成功
                wareSkuTo.setIsLock(true);
            }else {//锁定失败
                wareSkuTo.setIsLock(false);

            }

        }
        return lockWareSkuTo;
    }

    /**
     *
     * @param wareSkuLockVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean lockWaresku(WareSkuLockVo wareSkuLockVo) throws WareLockedException {

        /**
         * 保存库存工作单
         * 记录后续人工干预
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);

        List<CartItemVo> cartItemVos = wareSkuLockVo.getLocks();
        boolean isLocked = false;
        for (CartItemVo cartItemVo : cartItemVos) {
            List<WareSkuEntity> wareSkuEntities =
                    this.baseMapper.selectList(new LambdaQueryWrapper<WareSkuEntity>()
                            .eq(WareSkuEntity::getSkuId,cartItemVo.getSkuId()));
            if (wareSkuEntities==null||wareSkuEntities.size()==0) {
                throw new WareLockedException(cartItemVo.getSkuId());
            }
            List<Long> wareIds = wareSkuEntities.stream().map(wareSkuEntity -> {
                return wareSkuEntity.getId();
            }).collect(Collectors.toList());
            for (Long wareId : wareIds) {
                //锁库存
                Integer integer = this.baseMapper.lockWaresku(cartItemVo.getSkuId(), wareId, cartItemVo.getCount());
                if (integer==1) { //该sku 再该wareId 仓 锁库存成功
                    isLocked = true;
                    //锁定成功 保存库存任务详情单
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                    wareOrderTaskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskDetailEntity.setSkuId(cartItemVo.getSkuId());
                    wareOrderTaskDetailEntity.setSkuName(cartItemVo.getTitle());
                    wareOrderTaskDetailEntity.setLockStatus(StockDetailEnum.SKU_LOCK_LOCKED.getCode());
                    wareOrderTaskDetailEntity.setSkuNum(cartItemVo.getCount());
                    wareOrderTaskDetailEntity.setWareId(wareId);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    StockDetailTo stockDetailTo =  new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity,stockDetailTo);
                    stockLockedTo.setId(wareOrderTaskEntity.getId());
                    stockLockedTo.setDetailTo(stockDetailTo);
                    //锁定成功的sku 就发送消息
                    rabbitTemplate.convertAndSend(MQConstants.STOCK_EVENT_EXCHANGE
                            ,MQConstants.STOCK_LOCKED,stockLockedTo);
                    break;
                }else{
                    isLocked= false;
                }
                //否则继续尝试锁其他库存
            }

            if (isLocked==false){
                throw new WareLockedException(cartItemVo.getSkuId());
            }
        }
        return isLocked;
    }

    @Override
    @Transactional
    public void updateLockedSku(StockDetailTo detailTo) {
        this.baseMapper.updateLockedSku(detailTo.getSkuId(),detailTo.getWareId(),detailTo.getSkuNum());
    }

    @Override
    public Integer updateWareSku(Long skuId, Integer skuNum, Long wareId) {
        Integer res = this.baseMapper.updateWareSku(skuId,skuNum,wareId);
        return null;
    }

}