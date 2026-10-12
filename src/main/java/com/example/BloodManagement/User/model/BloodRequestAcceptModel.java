package com.example.BloodManagement.User.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloodRequestAcceptModel {

    @NotBlank(message = "Blood request ID is required")
    private String bloodRequestId;

    @NotNull(message = "Accepted unit is required")
    @Min(value = 1, message = "Accepted unit must be at least 1")
    private Integer acceptedUnit;
}