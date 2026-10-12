package com.example.BloodManagement.User.controller;

import com.example.BloodManagement.User.model.BloodRequestAcceptModel;
import com.example.BloodManagement.User.model.BloodRequestAcceptResponse;
import com.example.BloodManagement.User.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/acceptbloodrequest")
    public ResponseEntity<BloodRequestAcceptResponse> acceptBloodRequest(
            @Valid @RequestBody BloodRequestAcceptModel request,
            HttpServletRequest httpRequest
    ) {

        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            throw new IllegalStateException(
                    "Please login to accept a blood request"
            );
        }

        BloodRequestAcceptResponse response =
                userService.acceptBloodRequest(request, session);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleLoginError(
            IllegalStateException ex
    ) {

        return errorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleRoleError(
            SecurityException ex
    ) {

        return errorResponse(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleRequestError(
            IllegalArgumentException ex
    ) {

        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    private ResponseEntity<Map<String, Object>> errorResponse(
            HttpStatus status,
            String message
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("success", false);
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }
}