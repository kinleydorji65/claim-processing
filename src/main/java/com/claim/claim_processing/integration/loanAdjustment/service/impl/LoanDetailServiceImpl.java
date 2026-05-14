package com.claim.claim_processing.integration.loanAdjustment.service.impl;


import org.springframework.stereotype.Service;

import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDetailResponseDto;
import com.claim.claim_processing.integration.loanAdjustment.service.LoanDetailService;

import java.util.List;

@Service
public class LoanDetailServiceImpl implements LoanDetailService {

    @Override
    public List<LoanDetailResponseDto> getLoanDetails(String accountNumber) {
        return List.of(
                LoanDetailResponseDto.builder()
                        .loanName("Home Loan")
                        .status("Active")
                        .loanAmount(850000.00)
                        .outstandingAmount(420000.00)
                        .build(),

                LoanDetailResponseDto.builder()
                        .loanName("Vehicle Loan")
                        .status("Active")
                        .loanAmount(300000.00)
                        .outstandingAmount(82500.00)
                        .build(),

                LoanDetailResponseDto.builder()
                        .loanName("Education Loan")
                        .status("Completed")
                        .loanAmount(150000.00)
                        .outstandingAmount(0.00)
                        .build()
        );
    }
}
