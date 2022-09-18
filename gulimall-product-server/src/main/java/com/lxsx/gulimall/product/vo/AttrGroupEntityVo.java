package com.lxsx.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.lxsx.gulimall.product.entity.AttrGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttrGroupEntityVo extends AttrGroupEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 3级目录的根路径
     */
    Long[] catelogPath;


}

