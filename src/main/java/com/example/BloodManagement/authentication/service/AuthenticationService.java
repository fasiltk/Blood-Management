
package com.example.BloodManagement.authentication.service;

import com.example.BloodManagement.authentication.entity.Authentication;
import com.example.BloodManagement.authentication.model.*;
import com.example.BloodManagement.authentication.repository.AuthenticationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationRepository repository;

    public AuthenticationService(AuthenticationRepository repository) {
        this.repository = repository;
    }

    // REGISTER
    public AuthenticationResponse register(RegisterRequest request) {

        String username = request.getUsername().trim();

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "Password and confirm password do not match"
            );
        }

        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        Authentication user = new Authentication();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(
                request.getPassword(), BCrypt.gensalt(12)
        ));
        user.setName(request.getName().trim());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        repository.save(user);

        return new AuthenticationResponse(
                true, "Registration successful",
                user.getUsername(), user.getRole()
        );
    }

    // LOGIN
    public AuthenticationResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest,
            HttpSession session) {

        Authentication user = repository
                .findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid username or password"
                ));

        if (!BCrypt.checkpw(
                request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid username or password"
            );
        }

        // Rotate the session ID after successful login.
        httpRequest.changeSessionId();

        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        return new AuthenticationResponse(
                true, "Login successful",
                user.getUsername(), user.getRole()
        );
    }

    // CHANGE PASSWORD
    public AuthenticationResponse changePassword(
            ChangePasswordRequest request,
            HttpSession session) {

        String username = getSessionUsername(session);

        Authentication user = repository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "Logged-in user account was not found"
                ));

        if (!BCrypt.checkpw(
                request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (!request.getNewPassword().equals(
                request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        if (BCrypt.checkpw(
                request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(BCrypt.hashpw(
                request.getNewPassword(), BCrypt.gensalt(12)
        ));

        repository.save(user);

        return new AuthenticationResponse(
                true, "Password changed successfully",
                user.getUsername(), user.getRole()
        );
    }

    // LOGOUT
    public AuthenticationResponse logout(HttpSession session) {

        session.invalidate();

        return new AuthenticationResponse(
                true, "Logout successful", null, null
        );
    }

    private String getSessionUsername(HttpSession session) {

        Object username = session.getAttribute("username");

        if (username == null) {
            throw new IllegalStateException("User is not logged in");
        }

        return username.toString();
    }
}