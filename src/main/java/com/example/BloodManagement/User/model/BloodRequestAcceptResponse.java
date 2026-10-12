package com.example.BloodManagement.User.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BloodRequestAcceptResponse {

    private boolean success;

    private String message;

    private String id;

    private String bloodRequestId;

    private String acceptedUser;

    private Integer acceptedUnit;

    private LocalDateTime acceptedAt;
}