package com.shoriext.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostDto {

    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Size(min = 10, message = "Content must be at least 10 characters long")
    private String content;

    private String author;
}
