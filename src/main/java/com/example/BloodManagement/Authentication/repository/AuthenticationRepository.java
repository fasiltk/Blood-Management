
package com.example.BloodManagement.Authentication.repository;

import com.example.BloodManagement.Authentication.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationRepository
        extends JpaRepository<Authentication, String> {

    Optional<Authentication> findByUsername(String username);

    boolean existsByUsername(String username);
}