package com.shoriext.blog.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "verySecretKeyWhichShouldBeLongAndStoredSafelyInProduction";

    private Duration lifetime = Duration.ofHours(24);
}
