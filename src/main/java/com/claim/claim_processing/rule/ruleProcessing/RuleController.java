package com.claim.claim_processing.rule.ruleProcessing;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rule Controller", description = "Endpoints for rule evaluation and processing")
public class RuleController {

    private final RuleService ruleService;

    /**
     * Play with rule - Evaluate rules for a given claim request
     * 
     * @param request The claim initial preview request
     * @return List of matched sub-claim rules
     */
    @PostMapping("/play")
    @Operation(summary = "Play with rules", description = "Evaluate and return matched rules for a claim request")
    public ResponseEntity<ApiResponseDTO<List<MatchedSubClaimRuleDto>>> playWithRule(
            @RequestBody ClaimInitialPreviewRequest request) {

        ApiResponseDTO<List<MatchedSubClaimRuleDto>> response = ruleService.playWithRule(request);

        log.info("Found {} matching rules for NPPF: {}", response.getData().size(), request.getNppfNumber());
        return ResponseEntity.ok(response);
    }
}
