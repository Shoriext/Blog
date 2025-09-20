package com.shoriext.blog.controller;

import com.shoriext.blog.dto.CommentDto;
import com.shoriext.blog.dto.CommentResponse;
import com.shoriext.blog.dto.PagedResponse;
import com.shoriext.blog.model.Comment;
import com.shoriext.blog.service.CommentServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceImpl commentService;

    @GetMapping
    public ResponseEntity<PagedResponse<CommentResponse>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Comment> commentsPage = commentService.getCommentsByPostId(postId, pageable);

        Page<CommentResponse> responsePage = commentsPage.map(this::convertToResponse);
        return ResponseEntity.ok(new PagedResponse<>(responsePage));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentDto commentDto) {

        Comment comment = new Comment();
        comment.setText(commentDto.getText());

        Comment savedComment = commentService.createComment(postId, comment);
        return ResponseEntity.ok(convertToResponse(savedComment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto commentDto) {

        Comment commentDetails = new Comment();
        commentDetails.setText(commentDto.getText());

        Comment updatedComment = commentService.updateComment(commentId, commentDetails);
        return ResponseEntity.ok(convertToResponse(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    private CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getUser().getUsername(),
                comment.getPost().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
