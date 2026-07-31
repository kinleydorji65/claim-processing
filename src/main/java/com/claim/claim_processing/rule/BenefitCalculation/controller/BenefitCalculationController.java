package com.claim.claim_processing.rule.BenefitCalculation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.BenefitCalculation.CheckVestingService;
import com.claim.claim_processing.rule.BenefitCalculation.ForfeitedComponentService;
import com.claim.claim_processing.rule.BenefitCalculation.VerifierBenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.CheckVestingDto;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.dto.ForfeitedComponentResult;

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
    private final ForfeitedComponentService forfeitedComponentService;
    private final CheckVestingService checkVestingService;

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

    @PostMapping("/legal-check")
    public ResponseEntity<ApiResponseDTO<ForfeitedComponentResult>> forfeitedComponentService(@RequestBody ClaimInitialPreviewRequest request) {
        ForfeitedComponentResult response = forfeitedComponentService.processForfeitedAndVestingComponents(request);
        ApiResponseDTO<ForfeitedComponentResult> result = ApiResponseDTO.success(response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/legal-vesting-check")
    public ResponseEntity<ApiResponseDTO<CheckVestingDto>> checkVestingRules(@RequestBody ClaimInitialPreviewRequest request) {
        CheckVestingDto response = checkVestingService.checkVestingRules(request);
        ApiResponseDTO<CheckVestingDto> result = ApiResponseDTO.success(response);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Object>> getSpecialCaseBenefit(@RequestParam String nppfNumber, @RequestParam String isLegalRecovery) {
        ApiResponseDTO<Object> response = benefitCalculationService.getSpecialCaseBenefit(nppfNumber, isLegalRecovery);
        return ResponseEntity.ok(response);
    }
}
