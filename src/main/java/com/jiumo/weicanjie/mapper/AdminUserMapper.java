package com.jiumo.weicanjie.mapper;

import com.jiumo.weicanjie.entity.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminUserMapper {

    @Select("SELECT * FROM admin_user WHERE username = #{username} LIMIT 1")
    AdminUser selectByUsername(String username);

    @Insert("INSERT INTO admin_user(username, password, role, restaurant_id) " +
            "VALUES(#{username}, #{password}, #{role}, #{restaurantId})")
    void insertMerchant(AdminUser user);

    @Select("SELECT * FROM admin_user WHERE role = 'merchant' ORDER BY id DESC")
    List<AdminUser> selectMerchantList();

    @Delete("DELETE FROM admin_user WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE admin_user SET password = #{password} WHERE id = #{id}")
    int resetPassword(@Param("id") Long id, @Param("password") String password);
}
