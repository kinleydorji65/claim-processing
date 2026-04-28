package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityCategoryMapResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityCategoryMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-eligibility-category-map")
@RequiredArgsConstructor
public class ClaimEligibilityCategoryMapController {

    private final ClaimEligibilityCategoryMapService service;

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @PostMapping
    public ResponseEntity<ClaimEligibilityCategoryMapResponseDto> create(
            @RequestBody ClaimEligibilityCategoryMapRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // -------------------------------------------------
    // GET ALL
    // -------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ClaimEligibilityCategoryMapResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // -------------------------------------------------
    // GET BY RULE ID
    // -------------------------------------------------
    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<ClaimEligibilityCategoryMapResponseDto>> getByRuleId(
            @PathVariable Long ruleId) {

        return ResponseEntity.ok(service.getByRuleId(ruleId));
    }

    // -------------------------------------------------
    // DELETE
    // -------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ClaimEligibilityCategoryMapResponseDto>> getByCategoryId(
            @PathVariable String categoryId) {

        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }
}