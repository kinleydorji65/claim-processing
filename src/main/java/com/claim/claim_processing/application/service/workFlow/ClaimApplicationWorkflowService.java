package com.claim.claim_processing.application.service.workFlow;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;

import java.util.List;

public interface ClaimApplicationWorkflowService {

    ClaimApplicationWorkflowResponseDto create(
            ClaimApplicationWorkflowRequestDto request
    );

    ClaimApplicationWorkflowResponseDto getById(
            Long id
    );

    List<ClaimApplicationWorkflowResponseDto> getByClaimApplicationId(
            Long claimApplicationId
    );

    List<ClaimApplicationWorkflowResponseDto> getAll();
}