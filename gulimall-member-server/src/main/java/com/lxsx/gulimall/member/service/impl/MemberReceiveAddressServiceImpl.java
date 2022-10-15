package com.lxsx.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.member.dao.MemberReceiveAddressDao;
import com.lxsx.gulimall.member.entity.MemberReceiveAddressEntity;
import com.lxsx.gulimall.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> queryMemberReceiveAddressByMemberId(Long memberId) {

        List<MemberReceiveAddressEntity> entities =
                this.baseMapper.selectList(new LambdaQueryWrapper<MemberReceiveAddressEntity>().eq(MemberReceiveAddressEntity::getMemberId, memberId));

        return entities;
    }

    @Override
    public MemberReceiveAddressEntity queryMemberReceiveAddressByAddrId(Long addrId) {
        MemberReceiveAddressEntity entities =
                this.baseMapper.selectById(addrId);

        return entities;

    }

}