package com.shoriext.blog.service;

import java.util.List;
import java.util.Optional;

import com.shoriext.blog.model.Post;

public interface PostService {

    List<Post> getAllPosts();

    List<Post> getPostsByAuthor(String author);

    Optional<Post> getPostById(Long id);

    Post createPost(Post post);

    Post updatePost(Long id, Post postDetails);

    void deletePost(Long id);
}
