package com.shoriext.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentResponse {
    private Long id;
    private String text;
    private String author;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponse(Long id, String text, String author, Long postId,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.postId = postId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
