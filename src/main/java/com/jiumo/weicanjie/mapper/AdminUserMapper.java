package com.jiumo.weicanjie.mapper;

import com.jiumo.weicanjie.entity.AdminUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminUserMapper {

    @Select("SELECT * FROM admin_user WHERE username = #{username}")
    AdminUser selectByUsername(String username);

    @Insert("INSERT INTO admin_user(username, password, role, restaurant_id) " +
            "VALUES(#{username}, #{password}, #{role}, #{restaurantId})")
    void insertMerchant(AdminUser user);
}
