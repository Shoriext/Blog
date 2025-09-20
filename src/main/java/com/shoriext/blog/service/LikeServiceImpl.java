package com.shoriext.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoriext.blog.exception.ResourceNotFoundException;
import com.shoriext.blog.model.Like;
import com.shoriext.blog.model.Post;
import com.shoriext.blog.model.User;
import com.shoriext.blog.repository.LikeRepository;
import com.shoriext.blog.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public void likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = userService.getCurrentUser();

        if (likeRepository.existsByPostAndUser(post, currentUser)) {
            throw new RuntimeException("Post already liked by user");
        }

        Like like = new Like();

        like.setPost(post);
        like.setUser(currentUser);

        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = userService.getCurrentUser();

        likeRepository.deleteByPostAndUser(post, currentUser);
    }

    @Override
    public long getLikesCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return likeRepository.countByPost(post);
    }

    @Override
    public boolean isPostLikedByCurrentUser(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = userService.getCurrentUser();

        return likeRepository.existsByPostAndUser(post, currentUser);
    }

}
