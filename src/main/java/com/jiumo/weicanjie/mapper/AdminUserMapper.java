package com.jiumo.weicanjie.mapper;

import com.jiumo.weicanjie.entity.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 管理员用户数据访问接口（Mapper），提供对 admin_user 表的操作。
 * 包含账户查询、插入、删除、密码重置等功能。
 */
@Mapper
public interface AdminUserMapper {

    /**
     * 根据用户名查询管理员账户信息。
     *
     * @param username 管理员用户名
     * @return 返回匹配的管理员账户（如果存在）
     */
    @Select("SELECT * FROM admin_user WHERE username = #{username} LIMIT 1")
    AdminUser selectByUsername(String username);

    /**
     * 插入新管理员账户。
     *
     * @param user 管理员用户对象，包含用户名、密码、角色、餐厅ID等信息
     */
    @Insert("INSERT INTO admin_user(username, password, role, restaurant_id) " +
            "VALUES(#{username}, #{password}, #{role}, #{restaurantId})")
    void insertAccount(AdminUser user);

    /**
     * 查询所有角色为商家或后厨的管理员账户，按ID降序排列。
     *
     * @return 返回所有符合条件的管理员账户列表
     */
    @Select("SELECT * FROM admin_user WHERE role IN ('merchant','kitchen') ORDER BY id DESC")
    List<AdminUser> selectAllAccounts();

    /**
     * 根据管理员ID删除管理员账户。
     *
     * @param id 管理员账户ID
     * @return 返回删除的记录数，0表示未删除任何记录
     */
    @Delete("DELETE FROM admin_user WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 重置管理员账户密码。
     *
     * @param id       管理员账户ID
     * @param password 新密码
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE admin_user SET password = #{password} WHERE id = #{id}")
    int resetPassword(@Param("id") Long id, @Param("password") String password);
}
