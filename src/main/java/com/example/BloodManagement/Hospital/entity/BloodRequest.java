package com.example.BloodManagement.Hospital.entity;

import com.example.BloodManagement.Authentication.entity.Authentication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "BloodRequest")
@Getter
@Setter
public class BloodRequest {

    @Id
    @Column(length = 32, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "hospital",
            referencedColumnName = "username",
            nullable = false
    )
    private Authentication hospital;

    @Column(name = "blood_group", length = 3, nullable = false)
    private String bloodGroup;

    @Column(nullable = false)
    private Integer unit;

    // NEW COLUMN
    @Column(name = "priority", length = 1, nullable = false)
    private String priority;

    @Column(
            name = "requested_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime requestedAt;

    @PrePersist
    public void generateValues() {

        if (id == null) {
            id = UUID.randomUUID()
                    .toString()
                    .replace("-", "");
        }

        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        if (priority == null){
            priority = "N";
        }
    }
}