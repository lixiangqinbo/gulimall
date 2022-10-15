package com.lxsx.gulimall.constant;

public class ProductConstant {

    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"base","基本属性"),ATTR_TYPE_SALE(0,"sale","销售属性"),
        SEARCH_TYPE_UP(1,"search_type","需要检索"),
        SEARCH_TYPE_OFF(0,"search_type","不检索");
        //上架状态[0 - 下架，1 - 上架]
        private int type;
        private String note;
        private String typeName;

        AttrEnum(int type, String typeName, String note) {
            this.type = type;
            this.note = note;
            this.typeName = typeName;
        }

        public int getType() {
            return type;
        }

        public String getNote() {
            return note;
        }

        public String getTypeName() { return typeName; }
    }

    public enum SpuInfoEnum{

        PUBLISH_STATUS_NEW(0,"下架"),
        PUBLISH_STATUS_UP(1,"上架"),
        PUBLISH_STATUS_OFF(2,"上架"),
        DEFAULT_HOTSCORE(0,"默认热度评分"),
        CATELOG_LEVE_3(3,"三级目录"),
        CATELOG_LEVE_2(2,"二级目录"),
        CATELOG_LEVE_1(1,"一级目录");
        private int type;
        private  String note;

        SpuInfoEnum(int type, String note) {
            this.type = type;
            this.note = note;
        }

        public String getNote() {
            return note;
        }

        public int getType() {
            return type;
        }
    }

}
