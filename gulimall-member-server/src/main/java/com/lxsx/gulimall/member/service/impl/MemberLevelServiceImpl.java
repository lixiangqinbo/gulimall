package com.lxsx.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.member.constant.MemberEnum;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.member.dao.MemberLevelDao;
import com.lxsx.gulimall.member.entity.MemberLevelEntity;
import com.lxsx.gulimall.member.service.MemberLevelService;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public MemberLevelEntity queryDefaultLevel() {
        MemberLevelEntity memberLevelEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<MemberLevelEntity>()
                .eq(MemberLevelEntity::getDefaultStatus, MemberEnum.MEMBER_DEFAULT_STATUS.getCode()));

        return memberLevelEntity;
    }

    @Override
    public MemberLevelEntity queryMemberLevelInfo(Long levelId) {
        MemberLevelEntity memberLevelEntity = this.baseMapper.selectById(levelId);

        return memberLevelEntity;
    }

}