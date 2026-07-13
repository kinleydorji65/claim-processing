package com.claim.claim_processing.integration.loanAdjustment.service.impl;


import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDetailResponseDto;
import com.claim.claim_processing.integration.loanAdjustment.service.LoanDetailService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanDetailServiceImpl implements LoanDetailService {

    @Override
    public ApiResponseDTO<List<LoanDetailResponseDto>> getLoanDetails(String accountNumber) {
        return ApiResponseDTO.success(List.of(
                LoanDetailResponseDto.builder()
                        .loanName("Housing Loan")
                        .outstandingAmount(new BigDecimal("200.00"))
                        .build(),

                LoanDetailResponseDto.builder()
                        .loanName("Vehicle Loan")
                        .outstandingAmount(new BigDecimal("200.00"))
                        .build(),

                LoanDetailResponseDto.builder()
                        .loanName("Education Loan")
                        .outstandingAmount(new BigDecimal("150.00"))
                        .build()
        ));
    }
}
