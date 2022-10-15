package com.lxsx.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class UserRegisterVo implements Serializable {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为null")
    @Length(min = 6,max = 18,message = "用户名必须在在6-18位！")
    private String userName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String code;

    @NotBlank(message = "密码不能为null")
    @Length(min = 6,max = 18,message = "密码长度不能在6-18位")
    private String password;
}
