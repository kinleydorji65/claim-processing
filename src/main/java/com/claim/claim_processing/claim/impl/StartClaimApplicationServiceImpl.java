package com.claim.claim_processing.claim.impl;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.service.application.ClaimApplicationService;
import com.claim.claim_processing.claim.StartClaimApplicationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StartClaimApplicationServiceImpl implements StartClaimApplicationService {
    
    private final ClaimApplicationService claimApplicationService;
    
    @Override
    @Transactional
    public ApiResponseDTO<GeneralClaimResponse> startClaimApplication(GeneralClaimCreateRequest request) {
        
        ClaimApplication claimApplicationResponse = claimApplicationService.create(request.getClaimApplication());
        return null;
    }
    
}
