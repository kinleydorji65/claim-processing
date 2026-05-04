package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimVestingRuleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/vesting-rules")
@RequiredArgsConstructor
public class ClaimVestingRuleMasterController {

    private final ClaimVestingRuleMasterService service;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<ClaimVestingRuleResponseDto> createRule(
            @RequestBody ClaimVestingRuleRequestDto requestDto) {

        return ResponseEntity.ok(service.createRule(requestDto));
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ClaimVestingRuleResponseDto> updateRule(
            @PathVariable Long id,
            @RequestBody ClaimVestingRuleRequestDto requestDto) {

        return ResponseEntity.ok(service.updateRule(id, requestDto));
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ClaimVestingRuleResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<List<ClaimVestingRuleResponseDto>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    // =========================
    // FILTERS
    // =========================

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ClaimVestingRuleResponseDto>> getByCategory(
            @PathVariable String categoryId) {

        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }

    @GetMapping("/refund/{refundId}")
    public ResponseEntity<List<ClaimVestingRuleResponseDto>> getByRefund(
            @PathVariable Long refundId) {

        return ResponseEntity.ok(service.getByRefundId(refundId));
    }

    @GetMapping("/rule-type/{ruleTypeId}")
    public ResponseEntity<List<ClaimVestingRuleResponseDto>> getByRuleType(
            @PathVariable Long ruleTypeId) {

        return ResponseEntity.ok(service.getByRuleTypeId(ruleTypeId));
    }

    @GetMapping("/cutoff/{cutoffId}")
    public ResponseEntity<List<ClaimVestingRuleResponseDto>> getByCutoff(
            @PathVariable Long cutoffId) {

        return ResponseEntity.ok(service.getByCutoffId(cutoffId));
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {

        service.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}