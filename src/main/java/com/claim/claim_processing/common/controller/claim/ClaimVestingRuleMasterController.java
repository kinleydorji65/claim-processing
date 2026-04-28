package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleMasterResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimVestingRuleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/vesting-rules")
@RequiredArgsConstructor
public class ClaimVestingRuleMasterController {

    private final ClaimVestingRuleMasterService service;

    // -----------------------------
    // CREATE RULE
    // -----------------------------
    @PostMapping
    public ResponseEntity<ClaimVestingRuleMasterResponseDto> createRule(
            @RequestBody ClaimVestingRuleMasterRequestDto requestDto) {

        return ResponseEntity.ok(service.createRule(requestDto));
    }

    // -----------------------------
    // UPDATE RULE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ClaimVestingRuleMasterResponseDto> updateRule(
            @PathVariable Long id,
            @RequestBody ClaimVestingRuleMasterRequestDto requestDto) {

        return ResponseEntity.ok(service.updateRule(id, requestDto));
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ClaimVestingRuleMasterResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // -----------------------------
    // GET ALL RULES
    // -----------------------------
    @GetMapping
    public ResponseEntity<List<ClaimVestingRuleMasterResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // -----------------------------
    // GET BY CATEGORY ID
    // -----------------------------
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ClaimVestingRuleMasterResponseDto>> getByCategoryId(
            @PathVariable String categoryId) {

        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }

    // -----------------------------
    // DELETE RULE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        service.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}