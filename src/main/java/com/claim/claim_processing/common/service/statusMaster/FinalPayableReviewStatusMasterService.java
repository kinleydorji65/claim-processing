package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.FinalPayableReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.FinalPayableReviewStatusResponseDto;

import java.util.List;

public interface FinalPayableReviewStatusMasterService {

    FinalPayableReviewStatusResponseDto create(FinalPayableReviewStatusRequestDto dto);

    FinalPayableReviewStatusResponseDto update(
            Long id,
            FinalPayableReviewStatusRequestDto dto
    );

    FinalPayableReviewStatusResponseDto getById(Long id);

    FinalPayableReviewStatusResponseDto getByCode(String code);

    List<FinalPayableReviewStatusResponseDto> getAll();

    List<FinalPayableReviewStatusResponseDto> getAllActive();

    void delete(Long id);
}