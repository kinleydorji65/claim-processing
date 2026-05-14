package com.claim.claim_processing.rule.claim.BenefitCalculation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/claim-processing/benefit-calculation")
@RequiredArgsConstructor
public class BenefitCalculationController {
    private final BenefitCalculationService benefitCalculationService;

    @PostMapping
    public ResponseEntity<?> getCalculationPreview(@RequestBody ClaimPreviewRequest request) {
        ApiResponseDTO<ClaimCalculationResponseDTO> response = benefitCalculationService.calculateBenefit(request);
        return ResponseEntity.ok(response);
    }
}
