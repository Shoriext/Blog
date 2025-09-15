package com.shoriext.blog.service;

import java.beans.Transient;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoriext.blog.dto.SignUpDto;
import com.shoriext.blog.exception.ResourceAlreadyExistsException;
import com.shoriext.blog.model.User;
import com.shoriext.blog.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerNewUser(SignUpDto signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }
    }

}
