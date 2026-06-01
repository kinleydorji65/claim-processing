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
                        .status("Active")
                        .loanAmount(new BigDecimal("850000.00"))
                        .outstandingAmount(new BigDecimal("420000.00"))
                        .build(),

                LoanDetailResponseDto.builder()
                        .loanName("Vehicle Loan")
                        .status("Active")
                        .loanAmount(new BigDecimal("300000.00"))
                        .outstandingAmount(new BigDecimal("82500.00"))
                        .build(),

                LoanDetailResponseDto.builder()
                        .loanName("Education Loan")
                        .status("Completed")
                        .loanAmount(new BigDecimal("150000.00"))
                        .outstandingAmount(new BigDecimal("0.00"))
                        .build()
        ));
    }
}
