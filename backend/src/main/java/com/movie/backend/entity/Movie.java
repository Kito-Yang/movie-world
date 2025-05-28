package com.movie.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Movie {
    private Integer id;
    private Integer movieId;
    private String name;
    private Integer turnout;
    private String type;
    private String origin;
    private LocalDateTime time;
    private Integer duration;
    private Integer year;
    private BigDecimal rate;
    private String premiereLocation;

    // 构造函数
    public Movie() {}

    public Movie(Integer movieId, String name, Integer turnout, String type,
                 String origin, LocalDateTime time, Integer duration, Integer year,
                 BigDecimal rate, String premiereLocation) {
        this.movieId = movieId;
        this.name = name;
        this.turnout = turnout;
        this.type = type;
        this.origin = origin;
        this.time = time;
        this.duration = duration;
        this.year = year;
        this.rate = rate;
        this.premiereLocation = premiereLocation;
    }

    // Getter 和 Setter 方法
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTurnout() { return turnout; }
    public void setTurnout(Integer turnout) { this.turnout = turnout; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public String getPremiereLocation() { return premiereLocation; }
    public void setPremiereLocation(String premiereLocation) { this.premiereLocation = premiereLocation; }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", name='" + name + '\'' +
                ", turnout=" + turnout +
                ", type='" + type + '\'' +
                ", origin='" + origin + '\'' +
                ", time=" + time +
                ", duration=" + duration +
                ", year=" + year +
                ", rate=" + rate +
                ", premiereLocation='" + premiereLocation + '\'' +
                '}';
    }
}