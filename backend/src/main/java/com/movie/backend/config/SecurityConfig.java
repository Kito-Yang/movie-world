package com.movie.backend.config;

import com.movie.backend.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 启用CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 允许所有OPTIONS请求（CORS预检请求）
                        .requestMatchers("OPTIONS", "/**").permitAll()

                        // 允许注册和登录接口
                        .requestMatchers("/api/auth/**").permitAll()

                        // 允许查询接口（GET请求）
                        .requestMatchers("GET", "/api/movies/**").permitAll()
                        .requestMatchers("GET", "/api/movies/types").permitAll()

                        // 需要管理员权限的接口
                        .requestMatchers("POST", "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers("/api/movies/admin/**").hasRole("ADMIN")

                        // 用户反馈接口权限配置
                        .requestMatchers("POST", "/api/feedback/submit").authenticated()
                        .requestMatchers("GET", "/api/feedback/my").authenticated()
                        .requestMatchers("/api/feedback/admin/**").hasRole("ADMIN")

                        // 用户收藏接口权限配置
                        .requestMatchers("/api/favorites/**").authenticated()

                        // 用户信息编辑接口权限配置
                        .requestMatchers("/api/user/**").authenticated()

                        // 用户活跃度统计接口权限配置
                        .requestMatchers("/api/admin/activity/**").hasRole("ADMIN")

                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}