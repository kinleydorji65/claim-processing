package com.claim.claim_processing.integration.loanAdjustment.controller;


import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDetailResponseDto;
import com.claim.claim_processing.integration.loanAdjustment.service.LoanDetailService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/loan-details")
@RequiredArgsConstructor
public class LoanDetailController {

    private final LoanDetailService service;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponseDTO<List<LoanDetailResponseDto>>> getLoanDetails(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                ApiResponseDTO.success(service.getLoanDetails(accountNumber))
        );
    }
}
