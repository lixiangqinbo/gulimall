package com.lxsx.gulimall.controller;

import com.lxsx.gulimall.service.CartService;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.vo.CartItemVo;
import com.lxsx.gulimall.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
public class CartController {

    @Resource
    private CartService cartService;


    /**
     *商品添加购物车
     * @param skuId sku的id
     * @param num 添加的数量
     * @return
     */
    @GetMapping("/addCartItem")
    public String addToCart(@RequestParam("skuId")Long skuId, @RequestParam("num")Integer num){
        try {
            CartItemVo cartItemVo = cartService.addToCart(skuId,num);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:http://cart.gulimall.com/cartSuccess.html?skuId="+skuId;
    }

    /**
     *查看购物车
     * @param skuId sku的id
     * @return
     */
    @GetMapping("/cartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId")Long skuId,Model model){
        CartItemVo cartItemVo = cartService.queryCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo);
        return "success";
    }

    @GetMapping("/cart.html")
    public String Cart(Model model) throws ExecutionException, InterruptedException {
        CartVo cartVo = cartService.queryCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    /**
     * http://cart.gulimall.com/checkItem?skuId=69&checked=1
     * GET
     * @param skuId
     * @param checked 被选中得状态
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("checked")Integer checked){
        cartService.updateSkuStatus(skuId,checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }


    /**
     *http://cart.gulimall.com/countItem?skuId=69&num=4
     * GET
     * @param skuId
     * @param num 被修改得数量
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer num){
        cartService.updateSkuCounts(skuId,num);

        return "redirect:http://cart.gulimall.com/cart.html";
    }


    /**删除通过skuId
     * http://cart.gulimall.com/deleteItem?skuId=69
     * Get
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteSkuItem(skuId);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 更具会员的Id 查询车里的items
     * @param memberId
     * @return
     */
    @ResponseBody
    @GetMapping("/queryCartItems/{memberId}")
    public R queryCartItems(@PathVariable("memberId") Long memberId){
        List<CartItemVo> cartItemVos = cartService.queryCartItemBymemberId(memberId);
        return R.ok().setData(cartItemVos);
    }

}
