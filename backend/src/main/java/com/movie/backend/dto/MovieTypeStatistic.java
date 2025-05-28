package com.movie.backend.dto;

public class MovieTypeStatistic {
    private String type;
    private Long count;

    public MovieTypeStatistic() {}

    public MovieTypeStatistic(String type, Long count) {
        this.type = type;
        this.count = count;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}