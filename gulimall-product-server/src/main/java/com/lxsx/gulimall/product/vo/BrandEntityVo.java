package com.lxsx.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandEntityVo {
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名
     */
    private String name;

}
