package com.claim.claim_processing.application.service.detail;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.WrongRemitance;

public interface WrongRemitanceService {

    /**
     * Create a new wrong remitance record
     */
    List<WrongRemitance> create(ClaimApplication claimApplication, List<WrongRemitanceRequestDTO> requests);

    /**
     * Update an existing wrong remitance record
     */
    List<WrongRemitance> update(ClaimApplication claimApplication, List<WrongRemitanceRequestDTO> requests);

    /**
     * Get wrong remitance by ID
     */
    WrongRemitance getById(Long id);

    /**
     * Get wrong remitance by claim application
     */
    List<WrongRemitance> getByClaimApplication(ClaimApplication claimApplication);
}