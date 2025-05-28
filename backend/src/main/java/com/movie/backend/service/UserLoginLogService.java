package com.movie.backend.service;

import com.movie.backend.dto.UserActivityStatistic;
import com.movie.backend.entity.User;
import com.movie.backend.entity.UserLoginLog;
import com.movie.backend.mapper.UserLoginLogMapper;
import com.movie.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLoginLogService {

    @Autowired
    private UserLoginLogMapper loginLogMapper;

    @Autowired
    private UserMapper userMapper;

    // 记录登录日志
    public void recordLogin(String username, String ipAddress, String userAgent) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            UserLoginLog loginLog = new UserLoginLog(
                    user.getId(),
                    username,
                    ipAddress,
                    userAgent
            );
            loginLogMapper.insert(loginLog);
        }
    }

    // 获取用户活跃时间分布
    public List<UserActivityStatistic> getUserActivityStatistics() {
        return loginLogMapper.getUserActivityStatistics();
    }

    // 获取指定时间范围内的用户活跃时间分布
    public List<UserActivityStatistic> getUserActivityStatisticsByTimeRange(String startTime, String endTime) {
        return loginLogMapper.getUserActivityStatisticsByTimeRange(startTime, endTime);
    }

    // 获取用户登录历史
    public List<UserLoginLog> getUserLoginHistory(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return loginLogMapper.findByUserId(user.getId());
    }

    // 获取所有登录日志（管理员）
    public List<UserLoginLog> getAllLoginLogs() {
        return loginLogMapper.findAllLoginLogs();
    }
}