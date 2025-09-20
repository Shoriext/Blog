package com.shoriext.blog.service;

import com.shoriext.blog.dto.SignUpDto;
import com.shoriext.blog.model.User;

public interface UserService {
    User registerNewUser(SignUpDto signUpDto);

    void addRoleToUser(Long userId, String role);
}
