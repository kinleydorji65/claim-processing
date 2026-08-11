package com.claim.claim_processing.integration.contribution.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.contribution.dto.RecalculateMemberRequestDTO;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceInitionResponse;
import com.claim.claim_processing.integration.contribution.dto.WrongRemitanceRecalculationResponse;
import com.claim.claim_processing.integration.contribution.service.WrongRemitanceContributionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/wrong-remitance/contributions")
@RequiredArgsConstructor
public class WrongRemitanceContributionController {
    
    private final WrongRemitanceContributionService wrongRemitanceContributionService;

    /**
     * Get contribution details for a member (optional year filter)
     * GET /api/wrong-remitance/contributions/member/NPPF12345?year=2024
     */
    @PostMapping("/member/nppf-numbers")
    public ResponseEntity<ApiResponseDTO<List<WrongRemitanceInitionResponse>>> getMemberContributions(
            @RequestParam(value = "year", required = false) String year,
            @RequestBody() List<String> nppfNumbers) {
        
        log.info("Getting contributions for NPPF: {}, Year: {}", nppfNumbers, year);
        
        ApiResponseDTO<List<WrongRemitanceInitionResponse>> contributions = 
                wrongRemitanceContributionService.getContributionDetailOfMembers(year, nppfNumbers);
        
        return ResponseEntity.ok(contributions);
    }

    /**
     * Recalculate wrong remitance for specific months
     * POST /api/wrong-remitance/contributions/recalculate/NPPF12345?year=2024
     * Body: ["MARCH", "APRIL"]
     */
    @PostMapping("/recalculate")
    public ResponseEntity<?> recalculateWrongRemitance(
            @RequestBody RecalculateMemberRequestDTO recalculateMemberRequestDTO) {
        
       
        
        ApiResponseDTO<List<WrongRemitanceRecalculationResponse>> response = 
                wrongRemitanceContributionService.recalculateWrongRemitance(recalculateMemberRequestDTO);
        
        return ResponseEntity.ok(response);
    }
}