
package com.example.BloodManagement.Authentication.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "Authentication")
@Data
@NoArgsConstructor
public class Authentication {

    @Id
    @Column(length = 32, nullable = false, updatable = false)
    private String id;

    @Column(length = 30, nullable = false, unique = true)
    private String username;

    @Column(length = 70, nullable = false)
    private String password;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 10, nullable = false)
    private String phone;

    @Column(length = 1, nullable = false)
    private String role;

    @PrePersist
    public void generateId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString().replace("-", "");
        }
    }
}