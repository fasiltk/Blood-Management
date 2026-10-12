package com.example.BloodManagement.Hospital.model;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloodRequestModel {

    @NotBlank(message = "Blood group is required")
    @Pattern(
            regexp = "^(A|B|AB|O)[+-]$",
            message = "Invalid blood group"
    )
    private String bloodGroup;

    @NotNull(message = "Unit is required")
    @Min(value = 1, message = "Unit must be at least 1")
    private Integer unit;
}