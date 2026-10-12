
package com.example.BloodManagement.Authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {

    private boolean success;
    private String message;
    private String username;
    private String role;
}