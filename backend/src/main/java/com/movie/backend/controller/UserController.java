package com.movie.backend.controller;

import com.movie.backend.dto.UpdateUserRequest;
import com.movie.backend.entity.User;
import com.movie.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // 编辑用户信息（不需要管理员权限）
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserRequest request,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean updated = userService.updateUserProfile(username, request);

            if (updated) {
                return ResponseEntity.ok().body("{\"message\": \"用户信息更新成功\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"用户信息更新失败\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // 获取当前用户详细信息
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);

            if (user != null) {
                // 清除密码信息
                user.setPassword(null);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}