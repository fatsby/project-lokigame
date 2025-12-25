package com.theliems.lokigame.infrastructure.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * Secret key for signing JWTs.
     */
    private String secret;

    /**
     * Access token expiration time in milliseconds.
     */
    private long expiration;

    /**
     * Refresh token expiration time in milliseconds.
     */
    private long refreshExpiration;
}
