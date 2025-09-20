package com.shoriext.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoriext.blog.service.LikeServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@Tag(name = "Лайки", description = "API для управления лайками постов")
@RequiredArgsConstructor
public class LikeController {
    private final LikeServiceImpl likeService;

    @Operation(summary = "Поставить лайк посту", description = "Добавляет лайк текущего пользователя к посту. Требуется аутентификация.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить количество лайков", description = "Возвращает общее количество лайков для поста.")

    @GetMapping("/count")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        long count = likeService.getLikesCount(postId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Проверить статус лайка", description = "Проверяет, поставил ли текущий пользователь лайк посту. Требуется аутентификация.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/status")
    public ResponseEntity<Boolean> isPostLiked(@PathVariable Long postId) {
        boolean isLiked = likeService.isPostLikedByCurrentUser(postId);
        return ResponseEntity.ok(isLiked);
    }

    @Operation(summary = "Убрать лайк с поста", description = "Удаляет лайк текущего пользователя с поста. Требуется аутентификация.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }
}
