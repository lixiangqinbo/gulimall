package com.lxsx.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    private String catalog1Id; //1 leve catalog
    private List<Catelog3Vo> catalog3List;//3 leve catalog
    private String id;
    private String name;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3Vo{

        private String catalog2Id;//2 leve catalog
        private  String id;
        private  String name;


    }
}
