package com.movie.backend.mapper;

import com.movie.backend.entity.UserFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {

    // 添加收藏
    @Insert("INSERT INTO user_favorites(user_id, username, movie_id, movie_name) " +
            "VALUES(#{userId}, #{username}, #{movieId}, #{movieName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserFavorite favorite);

    // 取消收藏
    @Delete("DELETE FROM user_favorites WHERE user_id = #{userId} AND movie_id = #{movieId}")
    int delete(@Param("userId") Integer userId, @Param("movieId") Integer movieId);

    // 查询用户收藏列表
    @Select("SELECT * FROM user_favorites WHERE user_id = #{userId} ORDER BY created_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "movieName", column = "movie_name"),
            @Result(property = "createdTime", column = "created_time")
    })
    List<UserFavorite> findByUserId(Integer userId);

    // 分页查询用户收藏列表
    @Select("SELECT * FROM user_favorites WHERE user_id = #{userId} ORDER BY created_time DESC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "movieName", column = "movie_name"),
            @Result(property = "createdTime", column = "created_time")
    })
    List<UserFavorite> findByUserIdWithPage(Integer userId);

    // 检查是否已收藏
    @Select("SELECT COUNT(*) FROM user_favorites WHERE user_id = #{userId} AND movie_id = #{movieId}")
    int checkFavorite(@Param("userId") Integer userId, @Param("movieId") Integer movieId);

    // 根据电影ID查询收藏记录
    @Select("SELECT * FROM user_favorites WHERE movie_id = #{movieId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "movieId", column = "movie_id"),
            @Result(property = "movieName", column = "movie_name"),
            @Result(property = "createdTime", column = "created_time")
    })
    List<UserFavorite> findByMovieId(Integer movieId);
}