package com.lxsx.gulimall.member.constant;

public enum MemberEnum {
    MEMBER_DEFAULT_STATUS(1,"默认会员等级");

    private int code;
    private String note;

    MemberEnum(int code, String note) {
        this.code = code;
        this.note = note;
    }

    public int getCode() {
        return code;
    }

    public String getNote() {
        return note;
    }
}
