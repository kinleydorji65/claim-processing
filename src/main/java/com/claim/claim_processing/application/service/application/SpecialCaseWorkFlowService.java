package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.request.application.GeneralSpecialCaseApplicationRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface SpecialCaseWorkFlowService {
    ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> createSpecialCaseWithApplication(GeneralSpecialCaseApplicationRequest request);
    ApiResponseDTO<GeneralSpecialCaseResponse> approveSpecialCase(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request);
    ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> patchSpecialCaseWithApplication(GeneralSpecialCaseApplicationRequest request);
    ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> rejectSpecialCase(String applicationNumber, String rejectedBy, String rejectedRemarks);
    ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> verifiedClaimActionClaimedBy(String applicationNumber, String claimedBy);
    ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> verifiedClaimActionUnClaimedBy(String applicationNumber, String unClaimedBy);
    ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getSpecialCaseUserCode(String userCode);
    ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getAllSpecialCase();
    ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getAllSpecialCaseWithClaimedBy(String claimedBy);
}
