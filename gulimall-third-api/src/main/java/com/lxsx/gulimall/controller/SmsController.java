package com.lxsx.gulimall.controller;

import com.lxsx.gulimall.component.AliyunSmsComponent;
import com.lxsx.gulimall.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/aliyun/sms")
public class SmsController {
    @Resource
    private AliyunSmsComponent aliyunSmsComponent;

    /**
     * 提供给别人的验证码发送组件
     * @param phone 手机号
     * @param code 需要发送的验证码
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
        aliyunSmsComponent.sendCode(phone, code);
        return R.ok();
    }

}
