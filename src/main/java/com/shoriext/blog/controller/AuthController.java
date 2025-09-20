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
import com.shoriext.blog.service.UserServiceImpl;

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
public class AuthController {

    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/singup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        userServiceImpl.registerNewUser(signUpDto);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<Post> posts = user.getPosts();
        List<PostResponse> postDtos = new ArrayList<>();

        for (Post post : posts) {
            PostResponse temPost = new PostResponse();
            temPost.setId(post.getId());
            temPost.setTitle(post.getTitle());
            temPost.setContent(post.getContent());
            temPost.setUsernmae(post.getUser().getUsername());
            temPost.setCreatedAt(post.getCreatedAt());
            temPost.setUpdatedAt(post.getUpdatedAt());
            postDtos.add(temPost);

        }
        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(),
                postDtos);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
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
