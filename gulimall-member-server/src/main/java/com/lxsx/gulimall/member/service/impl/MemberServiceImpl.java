package com.lxsx.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.member.entity.MemberLevelEntity;
import com.lxsx.gulimall.member.exception.PhoneException;
import com.lxsx.gulimall.member.exception.UserNameException;
import com.lxsx.gulimall.member.service.MemberLevelService;


import com.lxsx.gulimall.member.vo.LoginUserVo;
import com.lxsx.gulimall.member.vo.UserRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.member.dao.MemberDao;
import com.lxsx.gulimall.member.entity.MemberEntity;
import com.lxsx.gulimall.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Resource
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 非社交注册
     * @param userRegisterVo
     */
    @Override
    public void saveMember(UserRegisterVo userRegisterVo) {
        //检查手机号 和账号唯一
        checkPhoneUnique(userRegisterVo.getPhone());
        checkUserNameUnique(userRegisterVo.getUserName());

        MemberEntity memberEntity = new MemberEntity();
        //先查询除默认会员等级状态
        MemberLevelEntity memberLevelEntity = memberLevelService.queryDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setCreateTime(new Date());
        //密码加密算法
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(userRegisterVo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setNickname(userRegisterVo.getUserName());
        memberEntity.setUsername(userRegisterVo.getUserName());
        memberEntity.setMobile(userRegisterVo.getPhone());
        this.baseMapper.insert(memberEntity);

    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneException {
        Long aLong = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getMobile, phone));
        if (aLong>0){
            throw  new PhoneException();
        }

    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameException {
        Long aLong = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, userName));
        if (aLong>0){
            throw  new UserNameException();
        }
    }

    @Override
    public MemberEntity queryUserByuserinfo(LoginUserVo loginUserVo) {
        MemberEntity memberEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getUsername, loginUserVo.getLoginacct()).or().eq(MemberEntity::getMobile,loginUserVo.getLoginacct()));
        if (memberEntity==null) {
            return  null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(loginUserVo.getPassword(), memberEntity.getPassword());
        if (!matches) {
            return null;
        }

        return memberEntity;
    }

    @Override
    public BigDecimal queryMemberLevelFare(Long memberId) {
        MemberEntity memberEntity = this.baseMapper.selectById(memberId);
        MemberLevelEntity memberLevelEntity = memberLevelService.queryMemberLevelInfo(memberEntity.getLevelId());
        return memberLevelEntity.getFreeFreightPoint();
    }


}