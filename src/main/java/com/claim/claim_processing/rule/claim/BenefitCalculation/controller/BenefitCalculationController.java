package com.claim.claim_processing.rule.claim.BenefitCalculation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.contribution.PartialMemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.request.FinalContributionRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.FinalCalculateAmountResponseDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


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

    @GetMapping("/{nppfNumber}")
    public ResponseEntity<?> getPartialCalculationPreview(@PathVariable String nppfNumber) {
        ApiResponseDTO<PartialMemberContributionSummary> response = benefitCalculationService.getPartialContributionSummary(nppfNumber);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/final-amount")
    public ResponseEntity<?> finalCalculatedAmount(
            @RequestBody FinalContributionRequest request) {

        ApiResponseDTO<FinalCalculateAmountResponseDTO> response =
                benefitCalculationService.finalCalculatedAmount(request);

        return ResponseEntity.ok(response);
    }
}
