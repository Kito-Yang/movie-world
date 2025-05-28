package com.movie.backend.dto;

public class FeedbackReplyRequest {
    private String adminReply;
    private Integer status; // 0-待处理, 1-处理中, 2-已解决, 3-已关闭

    public FeedbackReplyRequest() {}

    public FeedbackReplyRequest(String adminReply, Integer status) {
        this.adminReply = adminReply;
        this.status = status;
    }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}