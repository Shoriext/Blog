package com.shoriext.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shoriext.blog.exception.AccessDeniedException;
import com.shoriext.blog.exception.ResourceNotFoundException;
import com.shoriext.blog.model.Comment;
import com.shoriext.blog.model.Post;
import com.shoriext.blog.model.User;
import com.shoriext.blog.repository.CommentRepository;
import com.shoriext.blog.repository.PostRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceImpl userService;

    @Override
    public Page<Comment> getCommentsByPostId(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    @Override
    @Transactional
    public Comment createComment(Long postId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = userService.getCurrentUser();

        comment.setPost(post);
        comment.setUser(currentUser);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, Comment commentDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User currentUser = userService.getCurrentUser();

        if (!comment.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Access denied: You can only edit your own comments");
        }

        comment.setText(commentDetails.getText());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User currentUser = userService.getCurrentUser();

        if (!comment.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Access denied: You can only edit your own comments");
        }

        commentRepository.delete(comment);
    }

    @Override
    public long getCommentsCountByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

}
