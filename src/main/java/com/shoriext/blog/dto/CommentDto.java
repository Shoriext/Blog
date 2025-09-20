package com.shoriext.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDto {
    @NotBlank(message = "Comment text cannot be empty")
    private String text;
}
