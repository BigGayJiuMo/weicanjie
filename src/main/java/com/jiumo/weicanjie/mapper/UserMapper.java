package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE openid = #{openid}")
    User findByOpenId(String openid);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO users (openid, nickname, avatar_url, phone, created_time, updated_time) " +
            "VALUES (#{openid}, #{nickname}, #{avatarUrl}, #{phone}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET nickname = #{nickname}, avatar_url = #{avatarUrl}, phone = #{phone}, updated_time = NOW() WHERE id = #{id}")
    int update(User user);
}
