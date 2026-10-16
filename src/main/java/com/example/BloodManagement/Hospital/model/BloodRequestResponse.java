package com.example.BloodManagement.Hospital.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BloodRequestResponse {

    private boolean success;

    private String message;

    private String id;

    private String hospital;

    private String bloodGroup;

    private Integer unit;

    // NEW FIELD
    private String priority;

    private LocalDateTime requestedAt;
}