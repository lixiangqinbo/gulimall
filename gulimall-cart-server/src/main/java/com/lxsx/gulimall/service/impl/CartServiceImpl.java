package com.lxsx.gulimall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lxsx.gulimall.constants.CarConstants;
import com.lxsx.gulimall.constants.RpcStatusCode;
import com.lxsx.gulimall.feign.SkuInfoFeignService;
import com.lxsx.gulimall.interceptor.CartInterceptor;
import com.lxsx.gulimall.service.CartService;
import com.lxsx.gulimall.to.SkuInfoTo;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.vo.CartItemVo;
import com.lxsx.gulimall.vo.CartVo;
import com.lxsx.gulimall.vo.UserInfoVo;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class CartServiceImpl implements CartService {
    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SkuInfoFeignService skuInfoFeignService;
    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        /**
         * 购物车item我们以hash类型存储在redis
         * 1. 远程查询 sku信息 远程调用
         * 2. sku的销售属性 远程调用
         * 3. 异步编排
         */

        //拿到该用户的操作item
        BoundHashOperations cartItemOps = getCartItemOps();
        //先判断是redis里面是否已经有改skuId的sku
        String item = (String)cartItemOps.get(skuId.toString());
        if (!StringUtils.isEmpty(item)) {
            // 有就只增加数量
            CartItemVo cartItemVo = JSON.parseObject(item, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount()+num);
            cartItemOps.put(skuId.toString(), JSON.toJSONString(cartItemVo));
            return  cartItemVo;
        }

        CartItemVo cartItemVo = new CartItemVo();
        CompletableFuture<Void> skuinfoFuture = CompletableFuture.runAsync(() -> {
            // 远程查询 sku信息 远程调用
            R res = skuInfoFeignService.querySkuinfo(skuId);
            if (res.getCode() == RpcStatusCode.RPC_SUCCESS_CODE) {
                SkuInfoTo skuInfo = res.getData("skuInfo", new TypeReference<SkuInfoTo>() {
                });
                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setSkuId(skuId);
                // todo 这里需要算总价嘛 num  * price
                cartItemVo.setPrice(skuInfo.getPrice().multiply(new BigDecimal(num)));
                cartItemVo.setTitle(skuInfo.getSkuTitle());
            }
        }, executor);

        CompletableFuture<Void> attrFuture = CompletableFuture.runAsync(() -> {
            // sku的销售属性 远程调用
            R skuinfo = skuInfoFeignService.skuSaleAttrValus(skuId);
            if (skuinfo.getCode() == RpcStatusCode.RPC_SUCCESS_CODE) {
                List<String> data = skuinfo.getData(new TypeReference<List<String>>() {
                });
                cartItemVo.setSkuAttrValues(data);
            }
        }, executor);
        //等待异步任务完成
        CompletableFuture.allOf(skuinfoFuture,attrFuture).get();
        //新增到redis
        String toJSONString = JSON.toJSONString(cartItemVo);
        cartItemOps.put(skuId.toString(),toJSONString);
        return cartItemVo;
    }

    /**
     * 查询购物车内容
     * @param skuId
     * @return
     */
    @Override
    public CartItemVo queryCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> boundHashOperations = getCartItemOps();
        String item =(String) boundHashOperations.get(skuId.toString());
        if (StringUtils.isEmpty(item)) {
            return null;
        }
        CartItemVo cartItemVo = JSON.parseObject(item, CartItemVo.class);
        return cartItemVo;
    }

    /**
     * 查询整个购物车数据
     * @return
     */
    @Override
    public CartVo queryCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        //判断是否登录
        UserInfoVo userInfoVo = CartInterceptor.threadLocal.get();
        //不管登录否 都需要获取临时购物车加购的数据
        String tempCarKey = userInfoVo.getUserKey();
        List<CartItemVo> tempCart = getTempCart(tempCarKey);

        if (userInfoVo.getUserId()!=null) { //已登录
            /**
             * 已登录就有需要考虑合并临时购物车数据
             */
            for (CartItemVo cartItemVo : tempCart) {
                addToCart(cartItemVo.getSkuId(), cartItemVo.getCount());
            }
            //合并购物车后 删除临时数据
            clearCartItemByKey(tempCarKey);
            String carKey = userInfoVo.getUserId().toString();
            List<CartItemVo> cartItemVos = getTempCart(carKey);
            cartVo.setItems(cartItemVos);
        }else { //未登录
            cartVo.setItems(tempCart);
        }

        //剩下的其他参数 countNum countType totalAmount;
        cartVo.setCountNum();
        cartVo.setCountType();
        cartVo.setTotalAmount();
        return cartVo;
    }

    /**
     * 更具key清除
     * @param carKey
     */
    @Override
    public void clearCartItemByKey(String carKey) {

        stringRedisTemplate.delete(CarConstants.CAR_KEY_PREFIX+carKey);
    }

    /**
     * 修改sku得选中状态
     * @param skuId
     * @param checked
     */
    @Override
    public void updateSkuStatus(Long skuId, Integer checked) {
        CartItemVo cartItemVo = queryCartItem(skuId);
        if (cartItemVo!=null) {
            cartItemVo.setCheck(checked==0?false:true);
            updateCartItemVoBySkuId(cartItemVo);
        }

    }

    /**
     * 修改by skuId
     * @param cartItemVo
     */
    @Override
    public void updateCartItemVoBySkuId(CartItemVo cartItemVo) {
        String skuId = cartItemVo.getSkuId().toString();
        //拿到该用户的操作item
        BoundHashOperations cartItemOps = getCartItemOps();
        //先判断是redis里面是否已经有改skuId的sku
        String item = (String)cartItemOps.get(skuId);
        if (!StringUtils.isEmpty(item)) {
            cartItemOps.put(skuId, JSON.toJSONString(cartItemVo));
        }
    }

    /**
     * 修改item得数量
     * @param skuId
     * @param num
     */
    @Override
    public void updateSkuCounts(Long skuId, Integer num) {
        CartItemVo cartItemVo = queryCartItem(skuId);
        if (cartItemVo!=null) {
            cartItemVo.setCount(num);
            updateCartItemVoBySkuId(cartItemVo);
        }
    }

    /**
     * 删除指定skuid
     * @param skuId
     */
    @Override
    public void deleteSkuItem(Long skuId) {
        BoundHashOperations cartItemOps = getCartItemOps();
        cartItemOps.delete(skuId.toString());

    }

    /**
     * 根据会员ID查查询购物车的购物项
     * @param memberId
     * @return
     */
    @Override
    public List<CartItemVo> queryCartItemBymemberId(Long memberId) {
        List<CartItemVo> cartItemVos = getTempCart(memberId.toString());
        return cartItemVos;
    }

    /**
     * 获取购物车的数据
     * @param carKey
     */
    private List<CartItemVo> getTempCart(String carKey) {
        BoundHashOperations cartItemOps = getCartItemOps(carKey);
        List values = cartItemOps.values();
        List<CartItemVo> items= new ArrayList<>();
        for (Object value : values) {
            CartItemVo cartItemVo = JSON.parseObject((String) value, CartItemVo.class);
            items.add(cartItemVo);
        }
        return items;
    }

    /**
     * 获取该用户的redis 购物车操作
     */
    private BoundHashOperations getCartItemOps() {
        UserInfoVo userInfoVo = CartInterceptor.threadLocal.get();
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = null;
        if (userInfoVo.getUserId()!=null) {
            stringObjectObjectBoundHashOperations =
                    stringRedisTemplate.boundHashOps(CarConstants.CAR_KEY_PREFIX + userInfoVo.getUserId());
        }else {
            stringObjectObjectBoundHashOperations =
                    stringRedisTemplate.boundHashOps(CarConstants.CAR_KEY_PREFIX + userInfoVo.getUserKey());
        }
        return stringObjectObjectBoundHashOperations;
    }

    /**
     * 获取该用户的redis 购物车操作
     *按照指点的cartKey获取
     */
    private BoundHashOperations getCartItemOps(String carKey) {
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations  =
                    stringRedisTemplate.boundHashOps(CarConstants.CAR_KEY_PREFIX + carKey);

        return stringObjectObjectBoundHashOperations;
    }
}
