package com.movie.backend.mapper;

import com.movie.backend.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    @Select("SELECT * FROM users WHERE username = #{username}")
    @Results({
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    User findByUsername(String username);

    // 根据邮箱查询用户
    @Select("SELECT * FROM users WHERE email = #{email}")
    @Results({
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    User findByEmail(String email);

    // 插入新用户
    @Insert("INSERT INTO users(username, password, email, role, status) " +
            "VALUES(#{username}, #{password}, #{email}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // 根据ID查询用户
    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results({
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    User findById(Integer id);

    // 更新用户信息
    @Update("UPDATE users SET email=#{email}, updated_time=NOW() WHERE id=#{id}")
    int updateUser(User user);

    // 更新用户状态
    @Update("UPDATE users SET status=#{status}, updated_time=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}