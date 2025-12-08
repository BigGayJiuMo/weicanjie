package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户数据访问接口（Mapper），提供对 users 表的操作。
 * 主要功能包括通过 OpenID 查询用户、通过 ID 查询用户、插入用户信息和更新用户信息等。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 插入一条新的用户记录。
     *
     * @param user 包含用户信息的 User 对象
     * @return 返回插入的记录数，0表示未插入任何记录
     */
    @Insert("INSERT INTO users (openid, nickname, avatar_url, phone, created_time, updated_time) " +
            "VALUES (#{openid}, #{nickname}, #{avatarUrl}, #{phone}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新指定用户的信息。
     *
     * @param user 包含更新信息的 User 对象
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE users SET nickname = #{nickname}, avatar_url = #{avatarUrl}, phone = #{phone}, updated_time = NOW() WHERE id = #{id}")
    int update(User user);
}
