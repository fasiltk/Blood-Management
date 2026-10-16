package com.example.BloodManagement.Hospital.repository;

import com.example.BloodManagement.Hospital.entity.BloodRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodRequestRepository
        extends JpaRepository<BloodRequest, String> {

    // Get all requests belonging to a hospital

    @Query("""
            SELECT b FROM BloodRequest b
            WHERE b.hospital.username = :username
            ORDER BY b.requestedAt DESC
            """)
    List<BloodRequest> findByHospitalUsername(
            @Param("username") String username
    );

    // Get one request belonging to a hospital

    @Query("""
            SELECT b FROM BloodRequest b
            WHERE b.id = :id
            AND b.hospital.username = :username
            """)
    Optional<BloodRequest> findByIdAndHospitalUsername(
            @Param("id") String id,
            @Param("username") String username
    );
}