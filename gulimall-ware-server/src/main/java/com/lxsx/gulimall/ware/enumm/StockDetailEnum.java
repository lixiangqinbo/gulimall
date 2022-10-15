package com.lxsx.gulimall.ware.enumm;

public enum  StockDetailEnum {
    SKU_LOCK_LOCKED(1,"已锁定库存"),
    SKU_LOCK_RELEASE(2,"已释放库存"),
    SKU_LOCK_DECREASE(3,"已经扣减库存");
    private int code;
    private String note;
    StockDetailEnum(int code, String note) {
        this.code = code;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public int getCode() {
        return code;
    }
}
