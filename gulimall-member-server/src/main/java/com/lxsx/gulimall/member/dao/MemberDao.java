package com.lxsx.gulimall.member.dao;

import com.lxsx.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:03:47
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
