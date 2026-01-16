package com.theliems.lokigame.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @NotBlank
    @Size(max = 50, message = "Email cannot exceed 50 characters.")
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters.")
    private String password;
}
