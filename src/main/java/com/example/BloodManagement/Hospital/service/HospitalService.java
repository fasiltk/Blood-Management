package com.example.BloodManagement.Hospital.service;

import com.example.BloodManagement.Authentication.entity.Authentication;
import com.example.BloodManagement.Authentication.repository.AuthenticationRepository;

import com.example.BloodManagement.Hospital.entity.BloodRequest;
import com.example.BloodManagement.Hospital.model.BloodRequestModel;
import com.example.BloodManagement.Hospital.model.BloodRequestResponse;
import com.example.BloodManagement.Hospital.repository.BloodRequestRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HospitalService {

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Transactional
    public BloodRequestResponse createBloodRequest(
            BloodRequestModel request,
            HttpSession session
    ) {

        // 1. Get username and role from session

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        // 2. Check login

        if (username == null || role == null) {
            throw new IllegalStateException(
                    "Please login to create a blood request"
            );
        }

        // 3. Check role

        if (!"H".equals(role)) {
            throw new SecurityException(
                    "Access denied. Only hospitals can create blood requests"
            );
        }

        // 4. Fetch hospital from Authentication table

        Authentication hospital = authenticationRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Hospital account not found"
                        )
                );

        // 5. Verify role from database

        if (!"H".equals(hospital.getRole())) {
            throw new SecurityException(
                    "Access denied. Hospital role required"
            );
        }

        // 6. Create blood request

        BloodRequest bloodRequest = new BloodRequest();

        bloodRequest.setHospital(hospital);
        bloodRequest.setBloodGroup(request.getBloodGroup());
        bloodRequest.setUnit(request.getUnit());

        // UUID and date/time generated automatically

        // 7. Save

        BloodRequest savedRequest =
                bloodRequestRepository.saveAndFlush(bloodRequest);

        // 8. Response

        return new BloodRequestResponse(
                true,
                "Blood request created successfully",
                savedRequest.getId(),
                hospital.getUsername(),
                savedRequest.getBloodGroup(),
                savedRequest.getUnit(),
                savedRequest.getRequestedAt()
        );
    }
}