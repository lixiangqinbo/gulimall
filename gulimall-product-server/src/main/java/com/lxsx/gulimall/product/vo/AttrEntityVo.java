package com.lxsx.gulimall.product.vo;

import com.lxsx.gulimall.product.entity.AttrEntity;
import com.lxsx.gulimall.product.entity.AttrGroupEntity;
import com.lxsx.gulimall.valid.ListValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttrEntityVo {

    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    @NotBlank(message = "不能为null或者‘’字符")
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    @ListValid(value = {0,1},message = "enable字段只能为0或者1")
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;

    @NotNull(message = "分组ID不能为空")
    private Long attrGroupId;
    /**
     * 3级目录的根路径
     */
    Long[] catelogPath;

    List<AttrGroupEntity> attrGroupEntities;

    /**
     * 目录名字
     */
    String catelogName;

    /**
     * 属性分组名字
     */
    String attrGroupName;
}

