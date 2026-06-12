package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimPatchRequest;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ClaimApplicationFlowService {
    ApiResponseDTO<GeneralClaimResponse> create(GeneralClaimCreateRequest request);
    ApiResponseDTO<GeneralClaimResponse> patch(GeneralClaimPatchRequest request);
    ApiResponseDTO<List<GeneralClaimResponse>> getAll();
    ApiResponseDTO<GeneralClaimResponse> findByApplicationId(String applicationId);
    ApiResponseDTO<List<GeneralClaimResponse>> findByNppfNumber(String nppfNumber);
}
