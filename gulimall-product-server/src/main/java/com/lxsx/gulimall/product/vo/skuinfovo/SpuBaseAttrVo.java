package com.lxsx.gulimall.product.vo.skuinfovo;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public  class SpuBaseAttrVo{
    /**
     * 属性分组的ID
     */
    private Long attrGroupId;
    private Long attrId;
    private String attrName;
    private String attrValue;
}
