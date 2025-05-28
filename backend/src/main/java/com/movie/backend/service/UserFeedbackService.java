package com.movie.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.movie.backend.common.PageResult;
import com.movie.backend.dto.FeedbackRequest;
import com.movie.backend.entity.User;
import com.movie.backend.entity.UserFeedback;
import com.movie.backend.mapper.UserFeedbackMapper;
import com.movie.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFeedbackService {

    @Autowired
    private UserFeedbackMapper feedbackMapper;

    @Autowired
    private UserMapper userMapper;

    // 提交反馈
    public UserFeedback submitFeedback(String username, FeedbackRequest request) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserFeedback feedback = new UserFeedback(
                user.getId(),
                username,
                request.getTitle(),
                request.getContent(),
                request.getType()
        );

        feedbackMapper.insert(feedback);
        return feedback;
    }

    // 分页查询所有反馈（管理员）
    public PageResult<UserFeedback> getAllFeedbacks(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserFeedback> feedbacks = feedbackMapper.findAllFeedbacksWithPage();
        PageInfo<UserFeedback> pageInfo = new PageInfo<>(feedbacks);

        return new PageResult<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages()
        );
    }

    // 查询用户的反馈列表
    public List<UserFeedback> getUserFeedbacks(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return feedbackMapper.findByUserId(user.getId());
    }

    // 管理员回复反馈
    public boolean replyFeedback(Integer feedbackId, String adminReply, Integer status) {
        return feedbackMapper.replyFeedback(feedbackId, adminReply, status) > 0;
    }

    // 根据状态查询反馈
    public List<UserFeedback> getFeedbacksByStatus(Integer status) {
        return feedbackMapper.findByStatus(status);
    }

    // 根据ID查询反馈
    public UserFeedback getFeedbackById(Integer id) {
        return feedbackMapper.findById(id);
    }
}