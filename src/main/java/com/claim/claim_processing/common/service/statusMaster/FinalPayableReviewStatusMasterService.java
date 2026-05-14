package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.FinalPayableReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.FinalPayableReviewStatusResponseDto;

import java.util.List;

public interface FinalPayableReviewStatusMasterService {

    ApiResponseDTO<FinalPayableReviewStatusResponseDto> create(FinalPayableReviewStatusRequestDto dto);

    ApiResponseDTO<FinalPayableReviewStatusResponseDto> update(
            Long id,
            FinalPayableReviewStatusRequestDto dto
    );

    ApiResponseDTO<FinalPayableReviewStatusResponseDto> getById(Long id);

    ApiResponseDTO<FinalPayableReviewStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<FinalPayableReviewStatusResponseDto>> getAll();

    ApiResponseDTO<List<FinalPayableReviewStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}