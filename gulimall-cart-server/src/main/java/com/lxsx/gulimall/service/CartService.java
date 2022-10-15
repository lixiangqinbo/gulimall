package com.lxsx.gulimall.service;

import com.lxsx.gulimall.vo.CartItemVo;
import com.lxsx.gulimall.vo.CartVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {

    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo queryCartItem(Long skuId);

    CartVo queryCart() throws ExecutionException, InterruptedException;

    void clearCartItemByKey(String carKey);


    void updateSkuStatus(Long skuId, Integer checked);

    void updateCartItemVoBySkuId(CartItemVo cartItemVo);


    void updateSkuCounts(Long skuId, Integer num);

    void deleteSkuItem(Long skuId);

    List<CartItemVo> queryCartItemBymemberId(Long memberId);
}
