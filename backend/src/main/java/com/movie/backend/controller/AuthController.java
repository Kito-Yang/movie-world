package com.movie.backend.controller;

import com.movie.backend.dto.LoginRequest;
import com.movie.backend.dto.LoginResponse;
import com.movie.backend.dto.RegisterRequest;
import com.movie.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        LoginResponse response = userService.register(request);
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取当前用户信息
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String username = userService.getUsernameFromToken(token);
            String role = userService.getRoleFromToken(token);

            if (userService.validateToken(token, username)) {
                return ResponseEntity.ok(new LoginResponse(token, username, role, "获取用户信息成功"));
            } else {
                return ResponseEntity.badRequest().body(new LoginResponse(null, null, null, "Token无效"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponse(null, null, null, "Token解析失败"));
        }
    }
}