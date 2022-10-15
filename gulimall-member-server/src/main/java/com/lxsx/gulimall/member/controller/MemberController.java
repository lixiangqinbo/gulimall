package com.lxsx.gulimall.member.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.lxsx.gulimall.exception.BizCodeEnume;
import com.lxsx.gulimall.member.vo.LoginUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.member.entity.MemberEntity;
import com.lxsx.gulimall.member.service.MemberService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;



/**
 * 会员
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:03:47
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息member/member
     */
    @RequestMapping("/info/{id}")
   //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 获取会员 等级信息
     * 信息member/member
     */
    @RequestMapping("/fare/{memberId}")
    public R fare(@PathVariable("memberId") Long memberId){
        BigDecimal res = memberService.queryMemberLevelFare(memberId);
        return R.ok().setData(res);
    }

}
