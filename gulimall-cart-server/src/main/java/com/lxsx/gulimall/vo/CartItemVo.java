package com.lxsx.gulimall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartItemVo {
    /**
     * SKU 信息
     */
    private Long skuId;
    //是否被选中 为结算
    private Boolean check = true;

    private String title;

    private String image;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;
    //单价
    private BigDecimal price;
    //购买的数量
     private Integer count;
    //总价
    private BigDecimal totalPrice;

    public CartItemVo(){

    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttrValues() {
        return skuAttrValues;
    }

    public void setSkuAttrValues(List<String> skuAttrValues) {
        this.skuAttrValues = skuAttrValues;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {

        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalPrice() {
        //计算总价
        BigDecimal totalPrice =
                new BigDecimal(this.count).multiply(this.price);
        return totalPrice;
    }


}
