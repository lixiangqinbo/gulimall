package com.lxsx.gulimall.constant;

public class WareConstant {

    public enum PurchaseStatusEnum {
        /**
         * 状态[0新建，1已分配，2正在采购，3已完成，4采购失败]
         */

        CREATE(0, "新键"), ASSIGNED(1, "已分配"),
        ACKONWLEGE(2, "已领取"), HASERROR(4, "有异常"), FINISH(3, "已完成"),
        DEFAULT_PRIOR(0,"默认优先级");
        private int type;
        private String note;

        PurchaseStatusEnum(int type, String typeName) {
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


    public enum PurchaseDetailStatusEnum {
        /**
         * 状态[0新建，1已分配，2正在采购，3已完成，4采购失败]
         */

        CREATE(0, "新键"), ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"), HASERROR(4, "采购失败"), FINISH(3, "已完成");
        private int type;
        private String note;



        PurchaseDetailStatusEnum(int type, String typeName) {
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

    public enum WareSkuEnum{
        DEFAULT_STOCK_LOCKED(0,"默认的锁定库存数");

        private int type;
        private String note;

        WareSkuEnum(int type,String note) {
            this.type = type;
            this.note =note;
        }

        public int getType() {
            return type;
        }

        public String getNote() {
            return note;
        }
    }
}
