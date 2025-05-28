package com.movie.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.movie.backend.common.PageResult;
import com.movie.backend.entity.Movie;
import com.movie.backend.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.movie.backend.dto.MovieTypeStatistic;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieMapper movieMapper;

    // 获取电影类型统计
    public List<MovieTypeStatistic> getMovieTypeStatistics() {
        return movieMapper.getMovieTypeStatistics();
    }

    // 获取所有电影类型
    public List<String> getAllMovieTypes() {
        return movieMapper.getAllMovieTypes();
    }

    public List<Movie> getAllMovies() {
        return movieMapper.findAll();
    }

    public Movie getMovieById(Integer id) {
        return movieMapper.findById(id);
    }

    public List<Movie> searchMoviesByName(String name) {
        return movieMapper.findByName(name);
    }

    public List<Movie> getMoviesByYear(Integer year) {
        return movieMapper.findByYear(year);
    }

    public List<Movie> getMoviesByType(String type) {
        return movieMapper.findByType(type);
    }

    public Movie addMovie(Movie movie) {
        movieMapper.insert(movie);
        return movie;
    }

    public Movie updateMovie(Movie movie) {
        movieMapper.update(movie);
        return movie;
    }

    public boolean deleteMovie(Integer id) {
        return movieMapper.deleteById(id) > 0;
    }

    // 新增的分页方法

    // 分页查询所有电影
    public PageResult<Movie> getAllMoviesWithPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Movie> movies = movieMapper.findAllWithPage();
        PageInfo<Movie> pageInfo = new PageInfo<>(movies);

        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages()
        );
    }

    // 分页根据名称搜索电影
    public PageResult<Movie> searchMoviesByNameWithPage(String name, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Movie> movies = movieMapper.findByNameWithPage(name);
        PageInfo<Movie> pageInfo = new PageInfo<>(movies);

        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages()
        );
    }

    // 分页根据年份查询电影
    public PageResult<Movie> getMoviesByYearWithPage(Integer year, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Movie> movies = movieMapper.findByYearWithPage(year);
        PageInfo<Movie> pageInfo = new PageInfo<>(movies);

        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages()
        );
    }

    // 分页根据类型查询电影
    public PageResult<Movie> getMoviesByTypeWithPage(String type, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Movie> movies = movieMapper.findByTypeWithPage(type);
        PageInfo<Movie> pageInfo = new PageInfo<>(movies);

        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages()
        );
    }

    // 多条件分页查询
    public PageResult<Movie> getMoviesByConditionsWithPage(String name, String type, Integer year,
                                                           String origin, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Movie> movies = movieMapper.findByConditionsWithPage(name, type, year, origin);
        PageInfo<Movie> pageInfo = new PageInfo<>(movies);

        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages()
        );
    }
}