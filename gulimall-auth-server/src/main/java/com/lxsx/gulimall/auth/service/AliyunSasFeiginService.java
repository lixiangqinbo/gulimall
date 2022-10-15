package com.lxsx.gulimall.auth.service;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("aliyun-third-api")
public interface AliyunSasFeiginService {

    /**
     * 提供给别人的验证码发送组件
     * @param phone 手机号
     * @param code 需要发送的验证码
     * @return
     */
    @GetMapping("/aliyun/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
