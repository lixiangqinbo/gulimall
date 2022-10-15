package com.lxsx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.member.exception.PhoneException;
import com.lxsx.gulimall.member.exception.UserNameException;

import com.lxsx.gulimall.member.vo.LoginUserVo;
import com.lxsx.gulimall.member.vo.UserRegisterVo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.member.entity.MemberEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 会员
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:03:47
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveMember(UserRegisterVo userRegisterVo);

    void checkPhoneUnique(String phone) throws PhoneException;

    void checkUserNameUnique(String userName) throws UserNameException;

    MemberEntity queryUserByuserinfo(LoginUserVo loginUserVo);

    BigDecimal queryMemberLevelFare(Long memberId);
}

