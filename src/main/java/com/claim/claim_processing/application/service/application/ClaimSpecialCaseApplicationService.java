package com.claim.claim_processing.application.service.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface  ClaimSpecialCaseApplicationService {
    ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> create(
            ClaimSpecialCaseApplicationRequestDto dto, ClaimApplication claimApplication);
}
