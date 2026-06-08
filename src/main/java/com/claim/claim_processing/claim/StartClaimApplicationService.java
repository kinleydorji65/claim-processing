package com.claim.claim_processing.claim;

import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface StartClaimApplicationService {
    ApiResponseDTO<GeneralClaimResponse> startClaimApplication(GeneralClaimCreateRequest request);
}
