package com.theliems.lokigame.infrastructure.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Loki API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth") // Must match the name in @SecurityScheme
)
public class OpenApiGlobalConfig {
}