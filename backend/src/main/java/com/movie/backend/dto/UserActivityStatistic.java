package com.movie.backend.dto;

public class UserActivityStatistic {
    private Integer hour; // 小时 (0-23)
    private Long count;   // 登录次数

    public UserActivityStatistic() {}

    public UserActivityStatistic(Integer hour, Long count) {
        this.hour = hour;
        this.count = count;
    }

    public Integer getHour() { return hour; }
    public void setHour(Integer hour) { this.hour = hour; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}