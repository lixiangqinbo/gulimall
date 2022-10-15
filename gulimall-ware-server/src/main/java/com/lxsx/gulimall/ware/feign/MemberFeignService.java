package com.lxsx.gulimall.ware.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 获取会员 等级信息
     * 信息member/member
     */
    @RequestMapping("/member/member/fare/{memberId}")
    R fare(@PathVariable("memberId") Long memberId);
}

