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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Посты", description = "API для управления постами блога")
public class PostController {

    private final PostServiceImpl postService;

    @Operation(summary = "Получить все посты", description = "Возвращает список постов с пагинацией и сортировкой по дате создания (новые сначала).")
    @GetMapping
    public ResponseEntity<PagedResponse<PostResponse>> getAllPosts(
            int page,
            int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<Post> postsPage = postService.getAllPosts(pageable);

        Page<PostResponse> responsePage = postsPage.map(postService::convertToResponse);

        return ResponseEntity.ok(new PagedResponse<>(responsePage));
    }

    @Operation(summary = "Получить пост по ID", description = "Возвращает подробную информацию о посте по его идентификатору.")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found" + id));
        return ResponseEntity.ok(postService.convertToResponse(post));
    }

    @Operation(summary = "Создать новый пост", description = "Создает новый пост. Требуется аутентификация. Автор определяется автоматически из JWT токена.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostDto postDto) {

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());

        Post savedPost = postService.createPost(post);

        PostResponse response = postService.convertToResponse(savedPost);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить пост", description = "Обновляет существующий пост. Только автор поста или администратор могут обновлять.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostDto postDto) {

        // Конвертируем DTO в Entity
        Post postDetails = new Post();
        postDetails.setTitle(postDto.getTitle());
        postDetails.setContent(postDto.getContent());
        postDetails.setUser(postDto.getUser());

        Post updatePost = postService.updatePost(id, postDetails);
        return ResponseEntity.ok(postService.convertToResponse(updatePost));
    }

    @Operation(summary = "Удалить пост", description = "Удаляет пост по ID. Только автор поста или администратор могут удалять.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск постов", description = "Ищет посты по заголовку или содержанию. Возвращает результаты с пагинацией.")
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<PostResponse>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Post> postsPage = postService.searchPosts(query, pageable);
        Page<PostResponse> responsePage = postsPage.map(postService::convertToResponse);

        return ResponseEntity.ok(new PagedResponse<>(responsePage));
    }

    @Operation(summary = "Получить посты пользователя", description = "Возвращает все посты конкретного пользователя с пагинацией.")
    @GetMapping("/user/{username}")
    public ResponseEntity<PagedResponse<PostResponse>> getPostsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Post> postsPage = postService.getPostsByUsername(username, pageable);
        Page<PostResponse> responsePage = postsPage.map(postService::convertToResponse);

        return ResponseEntity.ok(new PagedResponse<>(responsePage));
    }

}
