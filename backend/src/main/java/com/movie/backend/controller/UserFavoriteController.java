package com.movie.backend.controller;

import com.movie.backend.common.PageResult;
import com.movie.backend.dto.FavoriteRequest;
import com.movie.backend.entity.UserFavorite;
import com.movie.backend.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService favoriteService;

    // 添加收藏
    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request,
                                         Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean success = favoriteService.addFavorite(username, request);

            if (success) {
                return ResponseEntity.ok().body("{\"message\": \"收藏成功\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"收藏失败\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // 取消收藏
    @DeleteMapping("/remove/{movieId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Integer movieId,
                                            Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean success = favoriteService.removeFavorite(username, movieId);

            if (success) {
                return ResponseEntity.ok().body("{\"message\": \"取消收藏成功\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"取消收藏失败\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // 获取用户收藏列表
    @GetMapping("/my")
    public ResponseEntity<List<UserFavorite>> getMyFavorites(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<UserFavorite> favorites = favoriteService.getUserFavorites(username);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 分页获取用户收藏列表
    @GetMapping("/my/page")
    public ResponseEntity<PageResult<UserFavorite>> getMyFavoritesWithPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            PageResult<UserFavorite> pageResult = favoriteService.getUserFavoritesWithPage(username, pageNum, pageSize);
            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 检查是否已收藏
    @GetMapping("/check/{movieId}")
    public ResponseEntity<?> checkFavorite(@PathVariable Integer movieId,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean isFavorite = favoriteService.isFavorite(username, movieId);
            return ResponseEntity.ok().body("{\"isFavorite\": " + isFavorite + "}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}