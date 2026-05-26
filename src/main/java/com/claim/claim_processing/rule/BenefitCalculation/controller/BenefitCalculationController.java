package com.claim.claim_processing.rule.BenefitCalculation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/claim-processing/benefit-calculation")
@RequiredArgsConstructor
public class BenefitCalculationController {
    private final BenefitCalculationService benefitCalculationService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ClaimCalculationResponseDTO>> calculateBenefit(@RequestBody ClaimInitialPreviewRequest request) {
        ApiResponseDTO<ClaimCalculationResponseDTO> response = benefitCalculationService.calculateBenefit(request);
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/total-accumulation/{memberCode}")
    // public ResponseEntity<?> getTotalAccumulationAmount(@PathVariable String memberCode) {
    //     ApiResponseDTO<BigDecimal> response = benefitCalculationService.getTotalAccumulationAmount(memberCode);
    //     return ResponseEntity.ok(response);
    // }
}
