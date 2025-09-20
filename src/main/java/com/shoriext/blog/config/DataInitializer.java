package com.shoriext.blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.shoriext.blog.dto.SignUpDto;
import com.shoriext.blog.model.User;
import com.shoriext.blog.service.UserService;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;

    Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Создаем администратора, если его нет
        try {
            SignUpDto adminDto = new SignUpDto();
            adminDto.setUsername("admin");
            adminDto.setEmail("admin@mail.com");
            adminDto.setPassword("adminpass");

            User admin = userService.registerNewUser(adminDto);

            userService.addRoleToUser(admin.getId(), "ROLE_ADMIN");
        } catch (Exception e) {
            logger.info("Admin user might already exist: " + e.getMessage());
        }
    }
}
