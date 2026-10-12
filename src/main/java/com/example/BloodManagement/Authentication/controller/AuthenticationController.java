
package com.example.BloodManagement.Authentication.controller;

import com.example.BloodManagement.Authentication.model.*;
import com.example.BloodManagement.Authentication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.register(request));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpSession session) {

        return ResponseEntity.ok(
                service.login(request, httpRequest, session)
        );
    }

    // CHANGE PASSWORD
    @PutMapping("/changepassword")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpSession session) {

        return ResponseEntity.ok(
                service.changePassword(request, session)
        );
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(
            HttpSession session) {

        return ResponseEntity.ok(service.logout(session));
    }

    // CHECK SESSION
    @GetMapping("/me")
    public ResponseEntity<AuthenticationResponse> currentUser(
            HttpSession session) {

        Object username = session.getAttribute("username");
        Object role = session.getAttribute("role");

        if (username == null || role == null) {
            throw new IllegalStateException("User is not logged in");
        }

        return ResponseEntity.ok(new AuthenticationResponse(
                true, "Session is active",
                username.toString(), role.toString()
        ));
    }

    // VALIDATION ERRORS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationError(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error :
                exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    error.getField(), error.getDefaultMessage()
            );
        }

        return ResponseEntity.badRequest().body(
                new ErrorResponse(false, "Validation failed", errors)
        );
    }

    // INVALID REQUEST OR LOGIN
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> badRequest(
            IllegalArgumentException exception) {

        String message = exception.getMessage();
        HttpStatus status = "Username already exists".equals(message)
                ? HttpStatus.CONFLICT
                : ("Invalid username or password".equals(message)
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(status).body(
                new ErrorResponse(false, message, null)
        );
    }

    // MISSING SESSION
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> unauthenticated(
            IllegalStateException exception) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(false, exception.getMessage(), null)
        );
    }

    // DATABASE CONFLICT
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> databaseConflict(
            DataIntegrityViolationException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        false,
                        "The request conflicts with existing database data",
                        null
                )
        );
    }

    // UNEXPECTED ERRORS
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpectedError(
            Exception exception) {

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(new ErrorResponse(
                false, "An unexpected server error occurred", null
        ));
    }
}