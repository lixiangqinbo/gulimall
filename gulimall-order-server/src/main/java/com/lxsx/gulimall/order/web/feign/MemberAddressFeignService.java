package com.lxsx.gulimall.order.web.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberAddressFeignService {

    /**
     * 通过memberId查询
     */
    @RequestMapping("/member/memberreceiveaddress/{memberId}/info")
    R memberAddressInfo(@PathVariable("memberId") Long memberId);

    /**
     * 通过addrId查询
     */
    @RequestMapping("/member/memberreceiveaddress/{addrId}/addrinfo")
    R memberAddressInfoByAddrId(@PathVariable("addrId") Long addrId);

    /**
     * 信息
     */
    @RequestMapping("/member/memberreceiveaddress/memberReceiveAddress/{id}")
    R memberReceiveAddress(@PathVariable("id") Long id);


}
