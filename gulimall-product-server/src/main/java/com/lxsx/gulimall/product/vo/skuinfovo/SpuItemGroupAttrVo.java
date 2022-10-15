package com.lxsx.gulimall.product.vo.skuinfovo;

import lombok.Data;

import java.util.List;

/**
 * 有关所有基础属性的vo
 * 里面组名
 * 改组下的所有属性名字
 */
@Data
public class SpuItemGroupAttrVo {
    /**
     * spu基础属性的groupid
     */
    private Long groupId;
    /**
     * 属性名称
     */
    private String groupName;
    /**
     * 所属组名下的属性值
     */
    private List<SpuBaseAttrVo> attrValues;


}
