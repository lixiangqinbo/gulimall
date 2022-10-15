package com.lxsx.gulimall.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 客户下发user-key
 */
@Data
@ToString
public class UserInfoVo {
    private Long userId;
    private String userKey;
    /**
     * 是否需要下放cookie
     */
    private  Boolean ifCookie = true;
}
