package com.lxsx.gulimall;

import com.lxsx.gulimall.product.dao.BrandDao;
import com.lxsx.gulimall.product.entity.BrandEntity;
import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.servlet.http.PushBuilder;
import java.util.List;

@SpringBootTest(classes = ProductServerApp.class)
class GulimallProductServerApplicationTests {

    @Resource
    private BrandDao brandDao;
    @Resource
    private CategoryService categoryService;

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

}
