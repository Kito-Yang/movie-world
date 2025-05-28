package com.movie.backend.entity;

import java.time.LocalDateTime;

public class UserFeedback {
    private Integer id;
    private Integer userId;
    private String username;
    private String title;
    private String content;
    private String type; // GENERAL, BUG, SUGGESTION
    private Integer status; // 0-待处理, 1-处理中, 2-已解决, 3-已关闭
    private String adminReply;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public UserFeedback() {}

    public UserFeedback(Integer userId, String username, String title, String content, String type) {
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = 0; // 默认待处理
    }

    // Getter 和 Setter 方法
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}