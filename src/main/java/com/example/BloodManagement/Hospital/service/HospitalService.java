package com.example.BloodManagement.Hospital.service;

import com.example.BloodManagement.Authentication.entity.Authentication;
import com.example.BloodManagement.Authentication.repository.AuthenticationRepository;

import com.example.BloodManagement.Hospital.entity.BloodRequest;
import com.example.BloodManagement.Hospital.model.BloodRequestModel;
import com.example.BloodManagement.Hospital.model.BloodRequestResponse;
import com.example.BloodManagement.Hospital.repository.BloodRequestRepository;

import com.example.BloodManagement.User.entity.BloodRequestAccept;
import com.example.BloodManagement.User.repository.BloodRequestAcceptRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HospitalService {

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private BloodRequestAcceptRepository bloodRequestAcceptRepository;


    // ---------------------------------------
    // CHECK HOSPITAL SESSION AND ROLE
    // ---------------------------------------

    private Authentication getLoggedInHospital(HttpSession session) {

        if (session == null) {
            throw new IllegalStateException(
                    "Please login as a hospital"
            );
        }

        String username =
                (String) session.getAttribute("username");

        String role =
                (String) session.getAttribute("role");

        if (username == null || role == null) {
            throw new IllegalStateException(
                    "Please login as a hospital"
            );
        }

        if (!"H".equals(role)) {
            throw new SecurityException(
                    "Access denied. Only hospitals can access this module"
            );
        }

        Authentication hospital = authenticationRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Hospital account not found"
                        )
                );

        if (!"H".equals(hospital.getRole())) {
            throw new SecurityException(
                    "Access denied. Hospital role required"
            );
        }

        return hospital;
    }


    // ---------------------------------------
    // CONVERT ENTITY TO RESPONSE
    // ---------------------------------------

    private BloodRequestResponse buildResponse(
            BloodRequest bloodRequest,
            String message
    ) {

        return new BloodRequestResponse(
                true,
                message,
                bloodRequest.getId(),
                bloodRequest.getHospital().getUsername(),
                bloodRequest.getBloodGroup(),
                bloodRequest.getUnit(),
                bloodRequest.getPriority(),
                bloodRequest.getRequestedAt()
        );
    }


    // ---------------------------------------
    // 1. CREATE BLOOD REQUEST
    // ---------------------------------------

    @Transactional
    public BloodRequestResponse createBloodRequest(
            BloodRequestModel request,
            HttpSession session
    ) {

        Authentication hospital =
                getLoggedInHospital(session);

        BloodRequest bloodRequest = new BloodRequest();

        bloodRequest.setHospital(hospital);
        bloodRequest.setBloodGroup(request.getBloodGroup());
        bloodRequest.setUnit(request.getUnit());
        bloodRequest.setPriority(request.getPriority());

        BloodRequest savedRequest =
                bloodRequestRepository.saveAndFlush(bloodRequest);

        return buildResponse(
                savedRequest,
                "Blood request created successfully"
        );
    }


    // ---------------------------------------
    // 2. VIEW MY BLOOD REQUESTS
    // ---------------------------------------

    @Transactional(readOnly = true)
    public List<BloodRequestResponse> getMyBloodRequests(
            HttpSession session
    ) {

        Authentication hospital =
                getLoggedInHospital(session);

        List<BloodRequest> requests =
                bloodRequestRepository.findByHospitalUsername(
                        hospital.getUsername()
                );

        return requests.stream()
                .map(request ->
                        buildResponse(
                                request,
                                "Blood request fetched successfully"
                        )
                )
                .toList();
    }


    // ---------------------------------------
    // 3. VIEW ONE BLOOD REQUEST
    // ---------------------------------------

    @Transactional(readOnly = true)
    public BloodRequestResponse getBloodRequestById(
            String id,
            HttpSession session
    ) {

        Authentication hospital =
                getLoggedInHospital(session);

        BloodRequest bloodRequest = bloodRequestRepository
                .findByIdAndHospitalUsername(
                        id,
                        hospital.getUsername()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Blood request not found or access denied"
                        )
                );

        return buildResponse(
                bloodRequest,
                "Blood request fetched successfully"
        );
    }


    // ---------------------------------------
    // 4. UPDATE BLOOD REQUEST
    // ---------------------------------------

    @Transactional
    public BloodRequestResponse updateBloodRequest(
            String id,
            BloodRequestModel request,
            HttpSession session
    ) {

        Authentication hospital =
                getLoggedInHospital(session);

        BloodRequest bloodRequest = bloodRequestRepository
                .findByIdAndHospitalUsername(
                        id,
                        hospital.getUsername()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Blood request not found or access denied"
                        )
                );

        // Calculate already accepted units

        int acceptedUnits = bloodRequestAcceptRepository
                .findAll()
                .stream()
                .filter(accept ->
                        accept.getBloodRequest()
                                .getId()
                                .equals(id)
                )
                .mapToInt(BloodRequestAccept::getAcceptedUnit)
                .sum();

        // Prevent reducing units below accepted units

        if (request.getUnit() < acceptedUnits) {
            throw new IllegalArgumentException(
                    "Cannot reduce units below accepted units: "
                            + acceptedUnits
            );
        }

        // Prevent changing blood group after acceptance

        if (acceptedUnits > 0 &&
                !bloodRequest.getBloodGroup()
                        .equals(request.getBloodGroup())) {

            throw new IllegalArgumentException(
                    "Cannot change blood group after users have accepted this request"
            );
        }

        bloodRequest.setBloodGroup(request.getBloodGroup());
        bloodRequest.setUnit(request.getUnit());
        bloodRequest.setPriority(request.getPriority());

        BloodRequest updatedRequest =
                bloodRequestRepository.saveAndFlush(bloodRequest);

        return buildResponse(
                updatedRequest,
                "Blood request updated successfully"
        );
    }


    // ---------------------------------------
    // 5. DELETE BLOOD REQUEST
    // ---------------------------------------

    @Transactional
    public String deleteBloodRequest(
            String id,
            HttpSession session
    ) {

        Authentication hospital =
                getLoggedInHospital(session);

        BloodRequest bloodRequest = bloodRequestRepository
                .findByIdAndHospitalUsername(
                        id,
                        hospital.getUsername()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Blood request not found or access denied"
                        )
                );

        // Check whether the request has acceptances

        boolean hasAcceptances = bloodRequestAcceptRepository
                .findAll()
                .stream()
                .anyMatch(accept ->
                        accept.getBloodRequest()
                                .getId()
                                .equals(id)
                );

        if (hasAcceptances) {
            throw new IllegalArgumentException(
                    "Cannot delete this blood request because users have already accepted it"
            );
        }

        bloodRequestRepository.delete(bloodRequest);

        return "Blood request deleted successfully";
    }
}