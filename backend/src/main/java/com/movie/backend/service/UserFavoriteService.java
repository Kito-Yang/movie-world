package com.movie.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.movie.backend.common.PageResult;
import com.movie.backend.dto.FavoriteRequest;
import com.movie.backend.entity.User;
import com.movie.backend.entity.UserFavorite;
import com.movie.backend.mapper.UserFavoriteMapper;
import com.movie.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteService {

    @Autowired
    private UserFavoriteMapper favoriteMapper;

    @Autowired
    private UserMapper userMapper;

    // 添加收藏
    public boolean addFavorite(String username, FavoriteRequest request) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查是否已收藏
        if (favoriteMapper.checkFavorite(user.getId(), request.getMovieId()) > 0) {
            throw new RuntimeException("已经收藏过此电影");
        }

        UserFavorite favorite = new UserFavorite(
                user.getId(),
                username,
                request.getMovieId(),
                request.getMovieName()
        );

        return favoriteMapper.insert(favorite) > 0;
    }

    // 取消收藏
    public boolean removeFavorite(String username, Integer movieId) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return favoriteMapper.delete(user.getId(), movieId) > 0;
    }

    // 获取用户收藏列表
    public List<UserFavorite> getUserFavorites(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return favoriteMapper.findByUserId(user.getId());
    }

    // 分页获取用户收藏列表
    public PageResult<UserFavorite> getUserFavoritesWithPage(String username, int pageNum, int pageSize) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        PageHelper.startPage(pageNum, pageSize);
        List<UserFavorite> favorites = favoriteMapper.findByUserIdWithPage(user.getId());
        PageInfo<UserFavorite> pageInfo = new PageInfo<>(favorites);

        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages()
        );
    }

    // 检查是否已收藏
    public boolean isFavorite(String username, Integer movieId) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return false;
        }

        return favoriteMapper.checkFavorite(user.getId(), movieId) > 0;
    }
}