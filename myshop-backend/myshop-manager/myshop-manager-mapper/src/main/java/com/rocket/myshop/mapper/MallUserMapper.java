package com.rocket.myshop.mapper;

import com.rocket.myshop.domain.MallUser;

import cn.jiebaba.summer.data.mapper.BaseMapper;

/**
 * mall_user 表数据访问，通用 CRUD 由 Summer BaseMapper 提供，复杂条件在 service 层用 Wrapper 构造。
 */
public interface MallUserMapper extends BaseMapper<MallUser> {
}
