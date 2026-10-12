
package com.example.BloodManagement.Authentication.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 30, message = "Username must not exceed 30 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])\\S{8,50}$",
            message = "Password must be 8-50 characters and contain uppercase, lowercase, digit and special character"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Name is required")
    @Size(max = 30, message = "Name must not exceed 30 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank(message = "Role is required")
    @Pattern(
            regexp = "^[UHB]$",
            message = "Role must be U, H or B"
    )
    private String role;
}