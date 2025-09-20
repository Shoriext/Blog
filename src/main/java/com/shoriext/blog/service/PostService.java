package com.shoriext.blog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shoriext.blog.model.Post;
import com.shoriext.blog.model.User;

public interface PostService {

    List<Post> getAllPosts();

    List<Post> getPostsByAuthor(User user);

    Optional<Post> getPostById(Long id);

    Post createPost(Post post);

    Post updatePost(Long id, Post postDetails);

    void deletePost(Long id);

    User getCurrentUser();

    Page<Post> getAllPosts(Pageable pageable);

    Page<Post> getPostsByAuthor(String author, Pageable pageable);

    Page<Post> searchPosts(String query, Pageable pageable);
}
