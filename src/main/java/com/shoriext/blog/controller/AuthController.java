package com.shoriext.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoriext.blog.dto.LoginDto;
import com.shoriext.blog.dto.PostResponse;
import com.shoriext.blog.dto.SignUpDto;
import com.shoriext.blog.dto.UserResponseDto;
import com.shoriext.blog.model.Post;
import com.shoriext.blog.model.User;
import com.shoriext.blog.repository.UserRepository;
import com.shoriext.blog.security.JwtUtils;
import com.shoriext.blog.service.LikeService;
import com.shoriext.blog.service.LikeServiceImpl;
import com.shoriext.blog.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для регистрации и аутентификации пользователей")
public class AuthController {

    private final UserServiceImpl userService;
    private final LikeServiceImpl likeService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя в системе. Пользователь получает роль ROLE_USER по умолчанию.")
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody SignUpDto signUpDto) {
        userService.registerNewUser(signUpDto);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @Operation(summary = "Получить текущего пользователя", description = "Возвращает информацию о текущем аутентифицированном пользователе на основе JWT токена.")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<Post> posts = user.getPosts();
        List<PostResponse> postDtos = new ArrayList<>();

        for (Post post : posts) {
            long likesCount = likeService.getLikesCount(post.getId());
            boolean isLikedByCurrentUser = likeService.isPostLikedByCurrentUser(post.getId());
            PostResponse temPost = PostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .username(post.getUser().getUsername())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .likesCount(likesCount)
                    .likedByCurrentUser(isLikedByCurrentUser)
                    .build();

            postDtos.add(temPost);

        }
        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(),
                postDtos);
        return ResponseEntity.ok(userResponseDto);
    }

    @Operation(summary = "Аутентификация пользователя", description = "Выполняет вход пользователя и возвращает JWT токен для доступа к защищенным endpoints.")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginDto loginDto) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwt = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(Map.of("token", jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

}
