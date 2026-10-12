package com.example.BloodManagement.User.service;

import com.example.BloodManagement.Authentication.entity.Authentication;
import com.example.BloodManagement.Authentication.repository.AuthenticationRepository;

import com.example.BloodManagement.Hospital.entity.BloodRequest;
import com.example.BloodManagement.Hospital.repository.BloodRequestRepository;

import com.example.BloodManagement.User.entity.BloodRequestAccept;
import com.example.BloodManagement.User.model.BloodRequestAcceptModel;
import com.example.BloodManagement.User.model.BloodRequestAcceptResponse;
import com.example.BloodManagement.User.repository.BloodRequestAcceptRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private BloodRequestAcceptRepository bloodRequestAcceptRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Transactional
    public BloodRequestAcceptResponse acceptBloodRequest(
            BloodRequestAcceptModel request,
            HttpSession session
    ) {

        // 1. Get username and role from session

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        // 2. Check login

        if (username == null || role == null) {
            throw new IllegalStateException(
                    "Please login to accept a blood request"
            );
        }

        // 3. Check user role

        if (!"U".equals(role)) {
            throw new SecurityException(
                    "Access denied. Only users can accept blood requests"
            );
        }

        // 4. Fetch user from Authentication

        Authentication user = authenticationRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User account not found"
                        )
                );

        // 5. Verify role from database

        if (!"U".equals(user.getRole())) {
            throw new SecurityException(
                    "Access denied. User role required"
            );
        }

        // 6. Fetch blood request

        BloodRequest bloodRequest = bloodRequestRepository
                .findById(request.getBloodRequestId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Blood request not found"
                        )
                );

        // 7. Create acceptance

        BloodRequestAccept accept = new BloodRequestAccept();

        accept.setBloodRequest(bloodRequest);
        accept.setAcceptedUser(user);
        accept.setAcceptedUnit(request.getAcceptedUnit());

        // UUID and date/time generated automatically

        // 8. Save

        BloodRequestAccept savedAccept =
                bloodRequestAcceptRepository.saveAndFlush(accept);

        // 9. Return response

        return new BloodRequestAcceptResponse(
                true,
                "Blood request accepted successfully",
                savedAccept.getId(),
                bloodRequest.getId(),
                user.getUsername(),
                savedAccept.getAcceptedUnit(),
                savedAccept.getAcceptedAt()
        );
    }
}