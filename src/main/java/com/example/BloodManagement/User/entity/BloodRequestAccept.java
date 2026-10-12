package com.example.BloodManagement.User.entity;

import com.example.BloodManagement.Authentication.entity.Authentication;
import com.example.BloodManagement.Hospital.entity.BloodRequest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "BloodRequestAccept")
@Getter
@Setter
public class BloodRequestAccept {

    @Id
    @Column(length = 32, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "blood_request_id",
            referencedColumnName = "id",
            nullable = false
    )
    private BloodRequest bloodRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "accepted_user",
            referencedColumnName = "username",
            nullable = false
    )
    private Authentication acceptedUser;

    @Column(name = "accepted_unit", nullable = false)
    private Integer acceptedUnit;

    @Column(name = "accepted_at", nullable = false, updatable = false)
    private LocalDateTime acceptedAt;

    @PrePersist
    public void generateValues() {

        if (id == null) {
            id = UUID.randomUUID()
                    .toString()
                    .replace("-", "");
        }

        if (acceptedAt == null) {
            acceptedAt = LocalDateTime.now();
        }
    }
}