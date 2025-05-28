package com.movie.backend.mapper;

import com.movie.backend.entity.UserFeedback;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFeedbackMapper {

    // 提交反馈
    @Insert("INSERT INTO user_feedback(user_id, username, title, content, type, status) " +
            "VALUES(#{userId}, #{username}, #{title}, #{content}, #{type}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserFeedback feedback);

    // 查询所有反馈（管理员用）
    @Select("SELECT * FROM user_feedback ORDER BY created_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "adminReply", column = "admin_reply"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<UserFeedback> findAllFeedbacks();

    // 分页查询反馈
    @Select("SELECT * FROM user_feedback ORDER BY created_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "adminReply", column = "admin_reply"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<UserFeedback> findAllFeedbacksWithPage();

    // 根据用户ID查询反馈
    @Select("SELECT * FROM user_feedback WHERE user_id = #{userId} ORDER BY created_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "adminReply", column = "admin_reply"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<UserFeedback> findByUserId(Integer userId);

    // 根据ID查询反馈
    @Select("SELECT * FROM user_feedback WHERE id = #{id}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "adminReply", column = "admin_reply"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    UserFeedback findById(Integer id);

    // 管理员回复反馈
    @Update("UPDATE user_feedback SET admin_reply=#{adminReply}, status=#{status}, updated_time=NOW() WHERE id=#{id}")
    int replyFeedback(@Param("id") Integer id, @Param("adminReply") String adminReply, @Param("status") Integer status);

    // 根据状态查询反馈
    @Select("SELECT * FROM user_feedback WHERE status = #{status} ORDER BY created_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "adminReply", column = "admin_reply"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<UserFeedback> findByStatus(Integer status);
}