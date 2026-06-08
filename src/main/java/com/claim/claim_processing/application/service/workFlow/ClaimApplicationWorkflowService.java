package com.claim.claim_processing.application.service.workFlow;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;

public interface ClaimApplicationWorkflowService {

    List<ClaimApplicationWorkflowResponseDto> create(ClaimApplication claimApplication,
            ClaimApplicationWorkflowRequestDto request
    );
    List<ClaimApplicationWorkflowResponseDto> getByApplicationId(Long applicationId);
}