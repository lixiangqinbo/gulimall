package com.lxsx.gulimall.auth.service;


import com.lxsx.gulimall.auth.vo.LoginUserVo;
import com.lxsx.gulimall.auth.vo.UserRegisterVo;
import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface RegisterFeignService {

    @PostMapping("/member/register")
    R register(@RequestBody UserRegisterVo userRegisterVo);

    @PostMapping("/member/login")
    R login(@RequestBody LoginUserVo loginUserVo);
}
