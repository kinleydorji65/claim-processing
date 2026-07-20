package com.claim.claim_processing.application.service.application;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationSummaryRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimPatchRequest;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

public interface ClaimApplicationFlowService {
    ApiResponseDTO<GeneralClaimResponse> create(GeneralClaimCreateRequest request);
    ApiResponseDTO<GeneralClaimResponse> patch(GeneralClaimPatchRequest request);
    ApiResponseDTO<List<GeneralClaimResponse>> getAll();
    ApiResponseDTO<GeneralClaimResponse> findByApplicationNumber(String applicationNumber);
    ApiResponseDTO<List<GeneralClaimResponse>> findByNppfNumber(String nppfNumber);
    ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> findByInitiatedByAndIsSpecialCase(
        String agencyCode, 
        ActivityEnum isSpecialCase, 
        Pageable page);
    ApiResponseDTO<GeneralClaimResponse> claimedBy(String applicationNumber, String claimedBy);
    ApiResponseDTO<GeneralClaimResponse> unClaimedBy(String applicationNumber, String unclaimedBy);
    ApiResponseDTO<List<GeneralClaimResponse>> findByUserCode(String userCode, Long statusId);
    ApiResponseDTO<List<GeneralClaimResponse>> getByAgencyCodeAndClaimTypeId(String agencyCode, Long claimTypeId);
    ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedApplication();
    ApiResponseDTO<List<GeneralClaimResponse>> verifiedClaimApplicationClaimedBy(String applicationNumber, String claimedBy);
    ApiResponseDTO<List<GeneralClaimResponse>> verifiedClaimApplicationUnClaimedBy(String applicationNumber, String unClaimedBy);
    
    ApiResponseDTO<GeneralClaimResponse> createForVerifier(Long applicationId, ClaimApplicationCalculationSummaryRequest request);
    
    ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedClaimButRejectedClaim();
    ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedClaimAndClaimedBy(String claimedBy);
    ApiResponseDTO<List<GeneralClaimResponse>> getClaimApplicationWhichClaimedBy(String claimedBy);
    ApiResponseDTO<List<GeneralClaimResponse>> getLegalRecoveryWithUserCode(String userCode);
    ApiResponseDTO<GeneralClaimResponse> rejectedClaimApplication(String applicationNumber, String rejectedBy, String remarks);
    ApiResponseDTO<List<GeneralClaimResponse>> getVerifiedClaim();
    ApiResponseDTO<GeneralClaimResponse> verifiedClaimActionRejectedByApprover(String applicationNumber,
             String rejectedBy, String rejectedRemarks);
    ApiResponseDTO<List<ClaimApplicationWorkflowResponseDto>> getWorkflowDetails(String applicationNumber);
}
