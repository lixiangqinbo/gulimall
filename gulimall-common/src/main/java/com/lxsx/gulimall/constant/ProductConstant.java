package com.lxsx.gulimall.constant;

public class ProductConstant {

    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");
        private int type;
        private String note;

        AttrEnum(int type, String note) {
            this.type = type;
            this.note = note;
        }

        public int getType() {
            return type;
        }

        public String getNote() {
            return note;
        }
    }
}
