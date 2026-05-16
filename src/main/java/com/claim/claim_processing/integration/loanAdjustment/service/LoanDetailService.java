package com.claim.claim_processing.integration.loanAdjustment.service;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDetailResponseDto;

public interface LoanDetailService {

    ApiResponseDTO<List<LoanDetailResponseDto>> getLoanDetails(String memberCode);
}
