package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimVestingRuleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/vesting-rules")
@RequiredArgsConstructor
public class ClaimVestingRuleMasterController {

    private final ClaimVestingRuleMasterService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ClaimVestingRuleResponseDto>> createRule(
            @RequestBody ClaimVestingRuleRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createRule(requestDto));
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClaimVestingRuleResponseDto>> updateRule(
            @PathVariable Long id,
            @RequestBody ClaimVestingRuleRequestDto requestDto) {

        return ResponseEntity.ok(service.updateRule(id, requestDto));
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClaimVestingRuleResponseDto>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ClaimVestingRuleResponseDto>>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    // -----------------------------
    // GET BY CATEGORY
    // -----------------------------
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponseDTO<List<ClaimVestingRuleResponseDto>>> getByCategory(
            @PathVariable String categoryId) {

        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }

    // -----------------------------
    // GET BY REFUND
    // -----------------------------
    @GetMapping("/refund/{refundId}")
    public ResponseEntity<ApiResponseDTO<List<ClaimVestingRuleResponseDto>>> getByRefund(
            @PathVariable Long refundId) {

        return ResponseEntity.ok(service.getByRefundId(refundId));
    }

    // -----------------------------
    // GET BY RULE TYPE
    // -----------------------------
    @GetMapping("/rule-type/{ruleTypeId}")
    public ResponseEntity<ApiResponseDTO<List<ClaimVestingRuleResponseDto>>> getByRuleType(
            @PathVariable Long ruleTypeId) {

        return ResponseEntity.ok(service.getByRuleTypeId(ruleTypeId));
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deleteRule(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.deleteRule(id));
    }
}