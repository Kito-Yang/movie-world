package com.movie.backend.entity;

import java.time.LocalDateTime;

public class UserFavorite {
    private Integer id;
    private Integer userId;
    private String username;
    private Integer movieId;
    private String movieName;
    private LocalDateTime createdTime;

    public UserFavorite() {}

    public UserFavorite(Integer userId, String username, Integer movieId, String movieName) {
        this.userId = userId;
        this.username = username;
        this.movieId = movieId;
        this.movieName = movieName;
    }

    // Getter 和 Setter 方法
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }

    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}