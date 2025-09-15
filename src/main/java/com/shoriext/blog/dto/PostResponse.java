package com.shoriext.blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String usernmae;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostResponse(
            Long id,
            String title,
            String content,
            String usernmae,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.usernmae = usernmae;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
