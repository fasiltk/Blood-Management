package com.example.BloodManagement.Hospital.controller;

import com.example.BloodManagement.Hospital.model.BloodRequestModel;
import com.example.BloodManagement.Hospital.model.BloodRequestResponse;
import com.example.BloodManagement.Hospital.service.HospitalService;

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
@RequestMapping("/api/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/bloodrequest")
    public ResponseEntity<BloodRequestResponse> createBloodRequest(
            @Valid @RequestBody BloodRequestModel request,
            HttpServletRequest httpRequest
    ) {

        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            throw new IllegalStateException(
                    "Please login to create a blood request"
            );
        }

        BloodRequestResponse response =
                hospitalService.createBloodRequest(request, session);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Validation errors

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

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    // Login errors

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleLoginError(
            IllegalStateException ex
    ) {

        return errorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }

    // Role errors

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleRoleError(
            SecurityException ex
    ) {

        return errorResponse(
                HttpStatus.FORBIDDEN,
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

        return ResponseEntity
                .status(status)
                .body(response);
    }
}