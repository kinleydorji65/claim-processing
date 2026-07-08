package com.claim.claim_processing.application.service.claimDetail;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface SpecialCaseService {
    GeneralSpecialCaseResponse createSpecialCase(GeneralSpecialCaseApplicationResponseDTO request);
}
