package com.shoriext.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.shoriext.blog.dto.PagedResponse;
import com.shoriext.blog.dto.PostDto;
import com.shoriext.blog.dto.PostResponse;
import com.shoriext.blog.exception.ResourceNotFoundException;
import com.shoriext.blog.model.Post;
import com.shoriext.blog.service.PostServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postServiceImpl;

    // GET /api/posts -> Получить все посты
    @GetMapping
    public ResponseEntity<PagedResponse<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<Post> postsPage = postServiceImpl.getAllPosts(pageable);

        Page<PostResponse> responsePage = postsPage.map(this::convertToResponse);

        return ResponseEntity.ok(new PagedResponse<>(responsePage));
    }

    // GET /api/posts/{id} -> Получить пост по ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        Post post = postServiceImpl.getPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found" + id));
        return ResponseEntity.ok(convertToResponse(post));
    }

    // POST /api/posts -> Создать новый пост
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostDto postDto) {
        // Конвертируем DTO в Entity
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());

        Post savedPost = postServiceImpl.createPost(post);

        PostResponse response = convertToResponse(savedPost);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PUT /api/posts/{id} -> Обновить пост
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto postDto) {

        // Конвертируем DTO в Entity
        Post postDetails = new Post();
        postDetails.setTitle(postDto.getTitle());
        postDetails.setContent(postDto.getContent());
        postDetails.setUser(postDto.getUser());

        Post updatePost = postServiceImpl.updatePost(id, postDetails);
        return ResponseEntity.ok(convertToResponse(updatePost));
    }

    // DELETE /api/posts/{id} -> Удалить пост
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postServiceImpl.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<PostResponse>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Post> postsPage = postServiceImpl.searchPosts(query, pageable);
        Page<PostResponse> responsePage = postsPage.map(this::convertToResponse);

        return ResponseEntity.ok(new PagedResponse<>(responsePage));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<PagedResponse<PostResponse>> getPostsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Post> postsPage = postServiceImpl.getPostsByUsername(username, pageable);
        Page<PostResponse> responsePage = postsPage.map(this::convertToResponse);

        return ResponseEntity.ok(new PagedResponse<>(responsePage));
    }

    // Вспомогательный метод для конвертации Entity в Response DTO
    private PostResponse convertToResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getUsername(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

}
