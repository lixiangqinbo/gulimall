package com.lxsx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lxsx.gulimall.constant.ProductConstant;
import com.lxsx.gulimall.product.service.CategoryService;
import com.lxsx.gulimall.product.vo.AttrAttrgroupRelationEntityVo;
import com.lxsx.gulimall.product.vo.AttrGroupEntityVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.product.entity.AttrGroupEntity;
import com.lxsx.gulimall.product.service.AttrGroupService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;



/**
 * 属性分组
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@RestController
@RequestMapping("product/attrgroup")
@Slf4j
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 列表 //传一个三级分类的id
     */
    @GetMapping("/list/{catelogId}")
   //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId){

        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("data", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
   //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){

		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        AttrGroupEntityVo attrGroupEntityVO = new AttrGroupEntityVo();
        BeanUtils.copyProperties(attrGroup, attrGroupEntityVO);

        //目录的完整路径
        Long[] catelogPath = categoryService.getCatelogPath(attrGroup.getCatelogId());
        attrGroupEntityVO.setCatelogPath(catelogPath);

        return R.ok().put("data", attrGroupEntityVO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }


   // http://localhost:8085/api/product/attrgroup/1/attr/relation?t=1663427364106

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){

        log.info("attrRelation方法拿到的参数：："+attrgroupId);
        PageUtils data = attrGroupService.queryLinkAttr(attrgroupId);
        return R.ok().put("data",data);
    }


    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrAttrgroupRelationEntityVo[] attrAttrgroupRelationEntityVo){
        if (attrAttrgroupRelationEntityVo==null||attrAttrgroupRelationEntityVo.length==0) {
            return R.error("参数为空！");
        }
        log.info("deleteRelation方法拿到的参数：："+attrAttrgroupRelationEntityVo.toString());
        attrGroupService.removeAttrRelation(attrAttrgroupRelationEntityVo);
        return R.ok();
    }

    //product/attrgroup/1/noattr/relation" 新增关联
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noattrRelation(@RequestParam Map<String,Object> params,@PathVariable Long attrgroupId){
        if (attrgroupId==null) {
            return R.error("参数为空！");
        }
        log.info("noattrRelation方法拿到的参数：："+attrgroupId+"::"+params.toString());
        PageUtils pageUtils = attrGroupService.queryNoRelationAttr(params,attrgroupId);
        return R.ok().put("page", pageUtils);
    }

    // url: this.$http.adornUrl("/product/attrgroup/attr/relation"),
    //          method: "post",

    @PostMapping("/attr/relation")
    public R attrRelation(@RequestBody AttrAttrgroupRelationEntityVo[] attrAttrgroupRelationEntityVo){
        if (attrAttrgroupRelationEntityVo==null||attrAttrgroupRelationEntityVo.length==0) {
            return R.error("参数为空！");
        }
        log.info("attrRelation方法拿到的参数：："+attrAttrgroupRelationEntityVo.toString());
        attrGroupService.saveGroupRelationBatch(attrAttrgroupRelationEntityVo);
        return R.ok();
    }
    /**
     * Request URL: http://localhost:8085/api/product/attrgroup/225/withattr?t=1663514543119
     * Request Method: GET
     */
    @GetMapping("/{catelogId}/withattr")
    public R withattr(@PathVariable("catelogId") Long catelogId){
        if (catelogId==null) {
            return R.error("参数为空！");
        }
        log.info("withattr：："+catelogId);
        List<AttrGroupEntityVo> data = attrGroupService.queryGroupWithAttrByCatId(
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getType(),catelogId);
        return R.ok().put("data", data);
    }

}
