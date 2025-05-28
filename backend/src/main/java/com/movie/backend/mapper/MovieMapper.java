package com.movie.backend.mapper;

import com.movie.backend.entity.Movie;
import com.movie.backend.dto.MovieTypeStatistic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MovieMapper {

    // 查询所有电影
    @Select("SELECT * FROM movies")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findAll();

    // 分页查询所有电影
    @Select("SELECT * FROM movies ORDER BY id DESC")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findAllWithPage();

    // 根据ID查询电影
    @Select("SELECT * FROM movies WHERE id = #{id}")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    Movie findById(Integer id);

    // 根据电影名称模糊查询
    @Select("SELECT * FROM movies WHERE name LIKE CONCAT('%', #{name}, '%')")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findByName(String name);

    // 分页根据电影名称模糊查询
    @Select("SELECT * FROM movies WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY id DESC")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findByNameWithPage(String name);

    // 插入新电影
    @Insert("INSERT INTO movies(movie_id, name, turnout, type, origin, time, duration, year, rate, premiere_location) " +
            "VALUES(#{movieId}, #{name}, #{turnout}, #{type}, #{origin}, #{time}, #{duration}, #{year}, #{rate}, #{premiereLocation})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Movie movie);

    // 更新电影信息
    @Update("UPDATE movies SET movie_id=#{movieId}, name=#{name}, turnout=#{turnout}, type=#{type}, " +
            "origin=#{origin}, time=#{time}, duration=#{duration}, year=#{year}, rate=#{rate}, " +
            "premiere_location=#{premiereLocation} WHERE id=#{id}")
    int update(Movie movie);

    // 删除电影
    @Delete("DELETE FROM movies WHERE id = #{id}")
    int deleteById(Integer id);

    // 根据年份查询电影
    @Select("SELECT * FROM movies WHERE year = #{year}")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findByYear(Integer year);

    // 分页根据年份查询电影
    @Select("SELECT * FROM movies WHERE year = #{year} ORDER BY id DESC")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findByYearWithPage(Integer year);

    // 根据类型查询电影
    @Select("SELECT * FROM movies WHERE type = #{type}")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findByType(String type);

    // 分页根据类型查询电影
    @Select("SELECT * FROM movies WHERE type = #{type} ORDER BY id DESC")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findByTypeWithPage(String type);

    // 多条件分页查询
    @Select("<script>" +
            "SELECT * FROM movies WHERE 1=1 " +
            "<if test='name != null and name != \"\"'>" +
            "AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='type != null and type != \"\"'>" +
            "AND type = #{type} " +
            "</if>" +
            "<if test='year != null'>" +
            "AND year = #{year} " +
            "</if>" +
            "<if test='origin != null and origin != \"\"'>" +
            "AND origin = #{origin} " +
            "</if>" +
            "ORDER BY id DESC" +
            "</script>")
    @Results({
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "premiereLocation", column = "premiere_location")
    })
    List<Movie> findByConditionsWithPage(@Param("name") String name,
                                         @Param("type") String type,
                                         @Param("year") Integer year,
                                         @Param("origin") String origin);

    // 电影类型统计
    @Select("SELECT type, COUNT(*) as count FROM movies WHERE type IS NOT NULL AND type != '' GROUP BY type ORDER BY count DESC")
    @Results({
            @Result(property = "type", column = "type"),
            @Result(property = "count", column = "count")
    })
    List<MovieTypeStatistic> getMovieTypeStatistics();

    // 获取所有不同的电影类型
    @Select("SELECT DISTINCT type FROM movies WHERE type IS NOT NULL AND type != '' ORDER BY type")
    List<String> getAllMovieTypes();
}