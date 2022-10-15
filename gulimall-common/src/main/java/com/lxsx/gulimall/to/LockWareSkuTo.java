package com.lxsx.gulimall.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class LockWareSkuTo implements Serializable {
    private static final long serialVersionUID = 1L;

    private  Long skuId;
    /**
     * 仓库Id
     */
    private Long wareId;
    /**
     *购买数量
     */
    private Integer num;
    /**
     * 锁定库存成功？
     */
    private Boolean isLock;
}
