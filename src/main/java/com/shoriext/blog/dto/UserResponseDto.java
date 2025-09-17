package com.shoriext.blog.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private List<PostResponse> posts;

    public UserResponseDto(Long id, String username, String email, List<PostResponse> posts) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.posts = posts;
    }
}
