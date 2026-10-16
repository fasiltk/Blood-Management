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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;


    // ---------------------------------------
    // 1. CREATE BLOOD REQUEST
    // ---------------------------------------

    @PostMapping("/blood-request")
    public ResponseEntity<BloodRequestResponse> createBloodRequest(
            @Valid @RequestBody BloodRequestModel request,
            HttpServletRequest httpRequest
    ) {

        HttpSession session = httpRequest.getSession(false);

        BloodRequestResponse response =
                hospitalService.createBloodRequest(request, session);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ---------------------------------------
    // 2. VIEW MY BLOOD REQUESTS
    // ---------------------------------------

    @GetMapping("/blood-requests")
    public ResponseEntity<List<BloodRequestResponse>> getMyBloodRequests(
            HttpServletRequest httpRequest
    ) {

        HttpSession session = httpRequest.getSession(false);

        return ResponseEntity.ok(
                hospitalService.getMyBloodRequests(session)
        );
    }


    // ---------------------------------------
    // 3. VIEW ONE BLOOD REQUEST
    // ---------------------------------------

    @GetMapping("/blood-request/{id}")
    public ResponseEntity<BloodRequestResponse> getBloodRequestById(
            @PathVariable String id,
            HttpServletRequest httpRequest
    ) {

        HttpSession session = httpRequest.getSession(false);

        return ResponseEntity.ok(
                hospitalService.getBloodRequestById(id, session)
        );
    }


    // ---------------------------------------
    // 4. UPDATE BLOOD REQUEST
    // ---------------------------------------

    @PutMapping("/blood-request/{id}")
    public ResponseEntity<BloodRequestResponse> updateBloodRequest(
            @PathVariable String id,
            @Valid @RequestBody BloodRequestModel request,
            HttpServletRequest httpRequest
    ) {

        HttpSession session = httpRequest.getSession(false);

        return ResponseEntity.ok(
                hospitalService.updateBloodRequest(
                        id,
                        request,
                        session
                )
        );
    }


    // ---------------------------------------
    // 5. DELETE BLOOD REQUEST
    // ---------------------------------------

    @DeleteMapping("/blood-request/{id}")
    public ResponseEntity<Map<String, Object>> deleteBloodRequest(
            @PathVariable String id,
            HttpServletRequest httpRequest
    ) {

        HttpSession session = httpRequest.getSession(false);

        String message =
                hospitalService.deleteBloodRequest(id, session);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("success", true);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }


    // ---------------------------------------
    // VALIDATION ERRORS
    // ---------------------------------------

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


    // ---------------------------------------
    // LOGIN ERRORS
    // ---------------------------------------

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleLoginError(
            IllegalStateException ex
    ) {

        return errorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }


    // ---------------------------------------
    // ROLE ERRORS
    // ---------------------------------------

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleRoleError(
            SecurityException ex
    ) {

        return errorResponse(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }


    // ---------------------------------------
    // REQUEST ERRORS
    // ---------------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleRequestError(
            IllegalArgumentException ex
    ) {

        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }


    // ---------------------------------------
    // COMMON ERROR RESPONSE
    // ---------------------------------------

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