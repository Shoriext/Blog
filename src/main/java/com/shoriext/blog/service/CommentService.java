package com.shoriext.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shoriext.blog.model.Comment;

public interface CommentService {
    Page<Comment> getCommentsByPostId(Long postId, Pageable pageable);

    Comment createComment(Long postId, Comment comment);

    Comment updateComment(Long commentId, Comment commentDetails);

    void deleteComment(Long commentId);

    long getCommentsCountByPostId(Long postId);
}