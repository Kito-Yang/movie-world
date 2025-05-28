package com.movie.backend.service;

import com.movie.backend.dto.LoginRequest;
import com.movie.backend.dto.LoginResponse;
import com.movie.backend.dto.RegisterRequest;
import com.movie.backend.entity.User;
import com.movie.backend.mapper.UserMapper;
import com.movie.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.movie.backend.dto.UpdateUserRequest;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserLoginLogService loginLogService;

    @Autowired
    private HttpServletRequest request;

    // 更新用户信息
    public boolean updateUserProfile(String username, UpdateUserRequest updateRequest) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查新邮箱是否已被使用（如果邮箱有变化）
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            if (!updateRequest.getEmail().equals(user.getEmail())) {
                User existingUser = userMapper.findByEmail(updateRequest.getEmail());
                if (existingUser != null) {
                    throw new RuntimeException("邮箱已被使用");
                }
            }
            user.setEmail(updateRequest.getEmail());
        }

        return userMapper.updateUser(user) > 0;
    }

    // 用户注册
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            return new LoginResponse(null, null, null, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && userMapper.findByEmail(request.getEmail()) != null) {
            return new LoginResponse(null, null, null, "邮箱已被使用");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER"); // 默认角色为普通用户
        user.setStatus(1);

        // 保存用户
        int result = userMapper.insert(user);
        if (result > 0) {
            // 生成 Token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            return new LoginResponse(token, user.getUsername(), user.getRole(), "注册成功");
        } else {
            return new LoginResponse(null, null, null, "注册失败");
        }
    }

    // 用户登录
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            return new LoginResponse(null, null, null, "用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            return new LoginResponse(null, null, null, "账户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new LoginResponse(null, null, null, "密码错误");
        }

        // 记录登录日志
        try {
            String ipAddress = getClientIpAddress();
            String userAgent = this.request.getHeader("User-Agent");
            loginLogService.recordLogin(user.getUsername(), ipAddress, userAgent);
        } catch (Exception e) {
            // 记录日志失败不影响登录
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername(), user.getRole(), "登录成功");
    }

    // 获取客户端IP地址
    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    // 根据用户名获取用户
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    // 验证 Token
    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }

    // 从 Token 获取用户名
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    // 从 Token 获取角色
    public String getRoleFromToken(String token) {
        return jwtUtil.getRoleFromToken(token);
    }
}