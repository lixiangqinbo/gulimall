package com.lxsx.gulimall;

import com.lxsx.gulimall.product.dao.BrandDao;
import com.lxsx.gulimall.product.dao.SkuSaleAttrValueDao;
import com.lxsx.gulimall.product.entity.BrandEntity;
import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.AttrGroupService;
import com.lxsx.gulimall.product.service.CategoryService;
import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemSaleAttrVo;
import com.lxsx.gulimall.product.vo.skuinfovo.SpuItemGroupAttrVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(classes = ProductServerApp.class)
class GulimallProductServerApplicationTests {

    @Resource
    private BrandDao brandDao;
    @Resource
    private CategoryService categoryService;
    @Resource
    private SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Resource
    AttrGroupService attrGroupService;
    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("华为");
        brandDao.insert(brandEntity);
        System.out.println(brandEntity.getBrandId());

    }

    @Test
    public void listWithTree(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        System.out.println(categoryEntities.toString());
    }

    @Test
    public void selectSkuSaleValueBySkuIdTest(){

        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueDao.selectSkuSaleValueBySkuId(17L);
        System.out.println(skuItemSaleAttrVos.toString());
    }

    @Test
    public void queryAttrGropWithAttrsByCatIdTest(){

//        List<SpuItemGroupAttrVo> spuItemGroupAttrVos = attrGroupService.queryAttrGropWithAttrsByCatId(catalogId, spuId);
//        System.out.println(skuItemSaleAttrVos.toString());
    }

}
