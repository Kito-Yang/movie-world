package com.movie.backend.controller;

import com.movie.backend.common.PageResult;
import com.movie.backend.dto.FeedbackReplyRequest;
import com.movie.backend.dto.FeedbackRequest;
import com.movie.backend.entity.UserFeedback;
import com.movie.backend.service.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class UserFeedbackController {

    @Autowired
    private UserFeedbackService feedbackService;

    // 提交反馈（不需要管理员权限）
    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request,
                                            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserFeedback feedback = feedbackService.submitFeedback(username, request);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // 获取用户自己的反馈列表
    @GetMapping("/my")
    public ResponseEntity<List<UserFeedback>> getMyFeedbacks(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<UserFeedback> feedbacks = feedbackService.getUserFeedbacks(username);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 分页查询所有反馈（需要管理员权限）
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResult<UserFeedback>> getAllFeedbacks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResult<UserFeedback> pageResult = feedbackService.getAllFeedbacks(pageNum, pageSize);
            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 根据状态查询反馈（需要管理员权限）
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserFeedback>> getFeedbacksByStatus(@PathVariable Integer status) {
        try {
            List<UserFeedback> feedbacks = feedbackService.getFeedbacksByStatus(status);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 管理员回复反馈（需要管理员权限）
    @PutMapping("/admin/reply/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> replyFeedback(@PathVariable Integer id,
                                           @RequestBody FeedbackReplyRequest request) {
        try {
            boolean success = feedbackService.replyFeedback(id, request.getAdminReply(), request.getStatus());
            if (success) {
                return ResponseEntity.ok().body("{\"message\": \"回复成功\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"回复失败\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // 根据ID查询反馈详情（需要管理员权限）
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserFeedback> getFeedbackById(@PathVariable Integer id) {
        try {
            UserFeedback feedback = feedbackService.getFeedbackById(id);
            if (feedback != null) {
                return ResponseEntity.ok(feedback);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}