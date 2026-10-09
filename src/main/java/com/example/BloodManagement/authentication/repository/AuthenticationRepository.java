
package com.example.BloodManagement.authentication.repository;

import com.example.BloodManagement.authentication.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationRepository
        extends JpaRepository<Authentication, String> {

    Optional<Authentication> findByUsername(String username);

    boolean existsByUsername(String username);
}