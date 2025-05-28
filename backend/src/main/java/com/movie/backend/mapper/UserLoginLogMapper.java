package com.movie.backend.mapper;

import com.movie.backend.entity.UserLoginLog;
import com.movie.backend.dto.UserActivityStatistic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserLoginLogMapper {

    // 记录登录日志
    @Insert("INSERT INTO user_login_log(user_id, username, ip_address, user_agent) " +
            "VALUES(#{userId}, #{username}, #{ipAddress}, #{userAgent})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserLoginLog loginLog);

    // 查询用户登录历史
    @Select("SELECT * FROM user_login_log WHERE user_id = #{userId} ORDER BY login_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "loginTime", column = "login_time"),
            @Result(property = "ipAddress", column = "ip_address"),
            @Result(property = "userAgent", column = "user_agent")
    })
    List<UserLoginLog> findByUserId(Integer userId);

    // 查询用户活跃时间分布（按小时统计）
    @Select("SELECT HOUR(login_time) as hour, COUNT(*) as count " +
            "FROM user_login_log " +
            "GROUP BY HOUR(login_time) " +
            "ORDER BY hour")
    List<UserActivityStatistic> getUserActivityStatistics();

    // 查询指定时间范围内的活跃时间分布
    @Select("SELECT HOUR(login_time) as hour, COUNT(*) as count " +
            "FROM user_login_log " +
            "WHERE login_time >= #{startTime} AND login_time <= #{endTime} " +
            "GROUP BY HOUR(login_time) " +
            "ORDER BY hour")
    List<UserActivityStatistic> getUserActivityStatisticsByTimeRange(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    // 查询所有登录日志
    @Select("SELECT * FROM user_login_log ORDER BY login_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "loginTime", column = "login_time"),
            @Result(property = "ipAddress", column = "ip_address"),
            @Result(property = "userAgent", column = "user_agent")
    })
    List<UserLoginLog> findAllLoginLogs();
}