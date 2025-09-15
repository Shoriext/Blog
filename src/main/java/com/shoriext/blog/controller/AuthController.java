package com.shoriext.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoriext.blog.dto.SignUpDto;
import com.shoriext.blog.service.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/singup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        userServiceImpl.registerNewUser(signUpDto);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

}
