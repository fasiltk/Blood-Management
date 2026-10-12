package com.example.BloodManagement.User.repository;

import com.example.BloodManagement.User.entity.BloodRequestAccept;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodRequestAcceptRepository
        extends JpaRepository<BloodRequestAccept, String> {

}