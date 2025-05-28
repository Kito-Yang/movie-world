package com.movie.backend.controller;

import com.movie.backend.dto.UserActivityStatistic;
import com.movie.backend.entity.UserLoginLog;
import com.movie.backend.service.UserLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activity")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class UserActivityController {

    @Autowired
    private UserLoginLogService loginLogService;

    // 获取用户活跃时间分布（需要管理员权限）
    @GetMapping("/statistics")
    public ResponseEntity<List<UserActivityStatistic>> getUserActivityStatistics() {
        try {
            List<UserActivityStatistic> statistics = loginLogService.getUserActivityStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取指定时间范围内的用户活跃时间分布（需要管理员权限）
    @GetMapping("/statistics/range")
    public ResponseEntity<List<UserActivityStatistic>> getUserActivityStatisticsByTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            List<UserActivityStatistic> statistics =
                    loginLogService.getUserActivityStatisticsByTimeRange(startTime, endTime);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取所有登录日志（需要管理员权限）
    @GetMapping("/logs")
    public ResponseEntity<List<UserLoginLog>> getAllLoginLogs() {
        try {
            List<UserLoginLog> logs = loginLogService.getAllLoginLogs();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}