package com.shoriext.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoriext.blog.service.LikeServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeServiceImpl likeService;

    @PostMapping
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        long count = likeService.getLikesCount(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isPostLiked(@PathVariable Long postId) {
        boolean isLiked = likeService.isPostLikedByCurrentUser(postId);
        return ResponseEntity.ok(isLiked);
    }

    @DeleteMapping
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }
}
