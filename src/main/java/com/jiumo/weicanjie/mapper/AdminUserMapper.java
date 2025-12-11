package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 管理员用户数据访问接口（Mapper），提供对 admin_user 表的操作。
 * 包含账号查询、创建、删除、重置密码、修改密码、绑定手机号等功能。
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}

