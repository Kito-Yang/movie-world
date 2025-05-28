package com.movie.backend.filter;

import com.movie.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 延迟初始化 UserService，避免循环依赖
        if (userService == null) {
            ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            if (context != null) {
                userService = context.getBean(UserService.class);
            }
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
        logger.debug("Authorization header: {}", authHeader);

        // 检查是否有 Authorization 头且以 "Bearer " 开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.debug("Extracted token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

            try {
                if (userService != null) {
                    username = userService.getUsernameFromToken(token);
                    logger.debug("Username from token: {}", username);
                }
            } catch (Exception e) {
                logger.warn("Token解析失败: {}", e.getMessage());
            }
        } else {
            logger.debug("No Authorization header or invalid format");
        }

        // 如果用户名不为空且当前没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Validating token for user: {}", username);

            // 验证 Token
            if (userService != null && userService.validateToken(token, username)) {
                String role = userService.getRoleFromToken(token);
                logger.debug("Token valid, user role: {}", role);

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                // 设置认证信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set for user: {}", username);
            } else {
                logger.warn("Token validation failed for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}