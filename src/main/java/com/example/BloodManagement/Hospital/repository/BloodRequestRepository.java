package com.example.BloodManagement.Hospital.repository;

import com.example.BloodManagement.Hospital.entity.BloodRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodRequestRepository
        extends JpaRepository<BloodRequest, String> {

}