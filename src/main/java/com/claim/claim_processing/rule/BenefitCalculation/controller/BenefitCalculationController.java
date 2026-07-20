package com.claim.claim_processing.rule.BenefitCalculation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.CaseTypeEnum;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.BenefitCalculation.VerifierBenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.SpecialCasePreviewResponse;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/claim-processing/benefit-calculation")
@RequiredArgsConstructor
public class BenefitCalculationController {
    private final BenefitCalculationService benefitCalculationService;
    private final VerifierBenefitCalculationService verifierBenefitCalculationService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ClaimCalculationResponseDTO>> calculateBenefit(@RequestBody ClaimInitialPreviewRequest request) {
        ApiResponseDTO<ClaimCalculationResponseDTO> response = benefitCalculationService.calculateBenefit(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifier-preview")
    public ResponseEntity<ApiResponseDTO<VerifierClaimCalculationResponseDTO>> verifierCalculateBenefit(@RequestBody ClaimInitialPreviewRequest request) {
        ApiResponseDTO<VerifierClaimCalculationResponseDTO> response = verifierBenefitCalculationService.calculateBenefit(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Object>> getSpecialCaseBenefit(@RequestParam CaseTypeEnum caseType, @RequestParam String nppfNumber) {
        ApiResponseDTO<Object> response = benefitCalculationService.getSpecialCaseBenefit(caseType, nppfNumber);
        return ResponseEntity.ok(response);
    }
}
