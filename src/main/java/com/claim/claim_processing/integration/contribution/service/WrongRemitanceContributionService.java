package com.claim.claim_processing.integration.contribution.service;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.contribution.dto.RecalculateMemberRequestDTO;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceInitionResponse;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceRecalculationResponse;

public interface WrongRemitanceContributionService {
    ApiResponseDTO<List<WrongRemitanceInitionResponse>> getContributionDetailOfMembers(String year, List<String> nppfNumbers);

    ApiResponseDTO<List<WrongRemitanceRecalculationResponse>> recalculateWrongRemitance(
            RecalculateMemberRequestDTO request);
}
