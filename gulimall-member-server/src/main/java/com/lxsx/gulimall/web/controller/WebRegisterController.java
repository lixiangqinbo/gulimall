package com.lxsx.gulimall.web.controller;

import com.lxsx.gulimall.exception.BizCodeEnume;
import com.lxsx.gulimall.member.entity.MemberEntity;
import com.lxsx.gulimall.member.service.MemberService;

import com.lxsx.gulimall.member.vo.LoginUserVo;
import com.lxsx.gulimall.member.vo.UserRegisterVo;
import com.lxsx.gulimall.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/member")
public class WebRegisterController {

    @Resource
    private MemberService memberService;



    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo){
        memberService.saveMember(userRegisterVo);

        return R.ok();

    }

    @PostMapping("/login")
    public R login(@RequestBody LoginUserVo loginUserVo){
        MemberEntity memberEntity = memberService.queryUserByuserinfo(loginUserVo);
        if (memberEntity==null) {
            return R.error(BizCodeEnume.ACCOUNT_PASSWORD_ERROR.getCode(),BizCodeEnume.ACCOUNT_PASSWORD_ERROR.getMsg() );
        }
        return R.ok().setData(memberEntity);
    }

}
