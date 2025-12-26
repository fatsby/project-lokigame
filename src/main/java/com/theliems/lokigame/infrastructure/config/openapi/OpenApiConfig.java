package com.theliems.lokigame.infrastructure.config.openapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth", // Arbitrary name, used to reference this scheme later
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT" // Optional, informs the user about the token format
)
public class OpenApiConfig {
    // No extra code needed here; the annotation adds the configuration
}
