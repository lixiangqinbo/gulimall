package com.lxsx.gulimall.member.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserRegisterVo implements Serializable {
    /**
     * 用户名
     */
    private String userName;

    private String phone;

    private String code;

    private String password;
}
