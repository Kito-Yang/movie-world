package com.movie.backend.controller;

import com.movie.backend.common.PageResult;
import com.movie.backend.entity.Movie;
import com.movie.backend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.movie.backend.dto.MovieTypeStatistic;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // 电影类型统计（需要管理员权限）
    @GetMapping("/admin/type-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovieTypeStatistic>> getMovieTypeStatistics() {
        try {
            List<MovieTypeStatistic> statistics = movieService.getMovieTypeStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取所有电影类型（公开接口）
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllMovieTypes() {
        try {
            List<String> types = movieService.getAllMovieTypes();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取所有电影
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    // 根据ID获取电影
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Integer id) {
        Movie movie = movieService.getMovieById(id);
        if (movie != null) {
            return ResponseEntity.ok(movie);
        }
        return ResponseEntity.notFound().build();
    }

    // 根据名称搜索电影
    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchMovies(@RequestParam String name) {
        List<Movie> movies = movieService.searchMoviesByName(name);
        return ResponseEntity.ok(movies);
    }

    // 根据年份获取电影
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Movie>> getMoviesByYear(@PathVariable Integer year) {
        List<Movie> movies = movieService.getMoviesByYear(year);
        return ResponseEntity.ok(movies);
    }

    // 根据类型获取电影
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Movie>> getMoviesByType(@PathVariable String type) {
        List<Movie> movies = movieService.getMoviesByType(type);
        return ResponseEntity.ok(movies);
    }

    // 添加新电影
    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.addMovie(movie);
        return ResponseEntity.ok(savedMovie);
    }

    // 更新电影信息
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Integer id, @RequestBody Movie movie) {
        movie.setId(id);
        Movie updatedMovie = movieService.updateMovie(movie);
        return ResponseEntity.ok(updatedMovie);
    }

    // 删除电影
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Integer id) {
        boolean deleted = movieService.deleteMovie(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 分页获取所有电影
    @GetMapping("/page")
    public ResponseEntity<PageResult<Movie>> getAllMoviesWithPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Movie> pageResult = movieService.getAllMoviesWithPage(pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    // 分页根据名称搜索电影
    @GetMapping("/search/page")
    public ResponseEntity<PageResult<Movie>> searchMoviesWithPage(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Movie> pageResult = movieService.searchMoviesByNameWithPage(name, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    // 分页根据年份获取电影
    @GetMapping("/year/{year}/page")
    public ResponseEntity<PageResult<Movie>> getMoviesByYearWithPage(
            @PathVariable Integer year,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Movie> pageResult = movieService.getMoviesByYearWithPage(year, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    // 分页根据类型获取电影
    @GetMapping("/type/{type}/page")
    public ResponseEntity<PageResult<Movie>> getMoviesByTypeWithPage(
            @PathVariable String type,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Movie> pageResult = movieService.getMoviesByTypeWithPage(type, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    // 多条件分页查询
    @GetMapping("/conditions/page")
    public ResponseEntity<PageResult<Movie>> getMoviesByConditionsWithPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String origin,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Movie> pageResult = movieService.getMoviesByConditionsWithPage(
                name, type, year, origin, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }
}