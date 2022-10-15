package com.lxsx.gulimall.vo;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

public class CartVo {

    private Boolean isChecked =true;

    /**
     * 购物车子项信息
     */
    List<CartItemVo> items;

    /**
     * 商品数量
     */
    private Integer countNum;

    /**
     * 商品类型数量
     */
    private Integer countType;

    /**
     * 商品总价
     */
    private BigDecimal totalAmount;

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    /**
     * 减免价格：默认不减价
     */
    private BigDecimal reduce = new BigDecimal("0.00");

    public CartVo(){ }

    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    /**
     * 计算出总共件数
     * @return
     */
    public void setCountNum() {
        int countNum =0;
        for (CartItemVo item : this.items) {
            countNum+=item.getCount();
        }
         this.countNum = countNum;
    }

    public Integer getCountNum() {
        return countNum;
    }

    /**
     * 商品类型数
     * @return
     */
    public void setCountType() {
        int countType = this.items == null ? 0 : this.items.size();
        this.countType = countType;
    }

    public Integer getCountType() {
        return countType;
    }

    /**
     * 计算出总价格
     * @return
     */
    public void setTotalAmount() {
        BigDecimal totalAmount =new BigDecimal(0);
        for (CartItemVo item : this.items) {
            if (item.getCheck()) {
                totalAmount = totalAmount.add(item.getTotalPrice());
            }
        }
        totalAmount.subtract(this.reduce);
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
