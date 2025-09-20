package com.shoriext.blog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shoriext.blog.exception.AccessDeniedException;
import com.shoriext.blog.exception.ResourceNotFoundException;
import com.shoriext.blog.model.Post;
import com.shoriext.blog.model.User;
import com.shoriext.blog.repository.PostRepository;
import com.shoriext.blog.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Post> getPostsByAuthor(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Post createPost(Post post) {
        post.setUser(getCurrentUser());
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        User currentUser = getCurrentUser();

        if (!post.getUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Access denied: You can only edit your own posts");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        User currentUser = getCurrentUser();

        if (!post.getUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Access denied: You can only edit your own posts");
        }

        postRepository.delete(post);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String userName = ((UserDetails) authentication.getPrincipal()).getUsername();

        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));
    }

    @Override
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<Post> getPostsByAuthor(String author, Pageable pageable) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author, pageable);
    }
}
