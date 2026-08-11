package com.claim.claim_processing.integration.contribution.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.contribution.dto.FullContributionHistoryResponse;
import com.claim.claim_processing.integration.contribution.service.AccountingYearSummaryService;
import com.claim.claim_processing.integration.contribution.service.FullContributionHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/full-contribution-history")
@RequiredArgsConstructor
public class FullContributionDetailController {
    
    private final FullContributionHistoryService fullContributionHistory;
    private final AccountingYearSummaryService accountingYearSummaryService;

    @GetMapping("/pf-statement/{nppfNumber}")
    public ResponseEntity<?> getFullContributionHistory(@PathVariable String nppfNumber) {
        ApiResponseDTO<FullContributionHistoryResponse> response = fullContributionHistory.getFullContributionHistory(nppfNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/visa-statement/{nppfNumber}")
    public ResponseEntity<ApiResponseDTO<FullContributionHistoryResponse>> getAccountingYearSummary(
            @PathVariable String nppfNumber) {

        ApiResponseDTO<FullContributionHistoryResponse> response = 
                accountingYearSummaryService.getAccountingYearSummary(nppfNumber);

        return ResponseEntity.ok(response);
    }
    
}
