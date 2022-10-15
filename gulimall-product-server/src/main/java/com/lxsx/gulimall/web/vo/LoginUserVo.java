package com.lxsx.gulimall.web.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserVo implements Serializable {
    /**
     * 账号
     */
    private String loginacct;
    /**
     * 密码
     */
    private String password;
}

