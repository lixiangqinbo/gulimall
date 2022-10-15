package com.lxsx.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class PurchaseFinishVo {
    /**
     * 采购单的id
     */
    @NotBlank(message = "不能为null")
    private Long id;
    /**
     * 采购项
     */
    private List<PurchaseDetailsVo> items;
}
