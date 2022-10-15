/**
  * Copyright 2022 bejson.com 
  */
package com.lxsx.gulimall.product.vo.productvo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2022-09-19 17:16:50
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@NoArgsConstructor
public class SpuSaveVo {

    @NotBlank(message = "spuName数据校验失败，不能为 null 或者 '' ")
    private String spuName;
    @NotBlank(message = "spuDescription数据校验失败，不能为 null 或者 '' ")
    private String spuDescription;
    @NotNull
    private Long catalogId;
    @NotNull
    private Long brandId;

    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;


}