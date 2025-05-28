package com.movie.backend.dto;

public class FeedbackRequest {
    private String title;
    private String content;
    private String type; // GENERAL, BUG, SUGGESTION

    public FeedbackRequest() {}

    public FeedbackRequest(String title, String content, String type) {
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}