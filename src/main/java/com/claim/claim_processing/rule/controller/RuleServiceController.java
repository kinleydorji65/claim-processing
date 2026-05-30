package com.claim.claim_processing.rule.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto;
import com.claim.claim_processing.rule.ruleGateWay.service.RuleGateWayService;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/rule-service")
public class RuleServiceController {
    private final RuleGateWayService ruleGateWayService;
    private final RuleService ruleService;
    
    @GetMapping("/{ruleId}")
    public ResponseEntity<ApiResponseDTO<RuleResponseDto>> test(@PathVariable Long ruleId) {
        return ResponseEntity.ok(ruleGateWayService.getByTopRuleType(ruleId));
    }

    

    @PostMapping("/preview")
    @Operation(
            summary = "Preview matched claim rule",
            description = "Fetch matched claim rule based on claim request, conditions, categories, components and refund types"
    )
    public ResponseEntity<ApiResponseDTO<List<MatchedSubClaimRuleDto>>> playWithRule(
            @RequestBody ClaimInitialPreviewRequest request
    ) {

        ApiResponseDTO<List<MatchedSubClaimRuleDto>> response =
                ruleService.playWithRule(request);

        return ResponseEntity.ok(response);
    }
}
