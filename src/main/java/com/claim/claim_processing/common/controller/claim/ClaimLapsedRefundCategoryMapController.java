package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundCategoryMapResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundCategoryMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims/lapsed-refund-category-map")
@RequiredArgsConstructor
public class ClaimLapsedRefundCategoryMapController {

    private final ClaimLapsedRefundCategoryMapService service;

    // =====================================================
    // CREATE
    // =====================================================
    @PostMapping
    public ResponseEntity<ClaimLapsedRefundCategoryMapResponseDto> create(
            @RequestBody ClaimLapsedRefundCategoryMapRequestDto requestDto
    ) {
        return ResponseEntity.ok(service.create(requestDto));
    }

    // =====================================================
    // UPDATE
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<ClaimLapsedRefundCategoryMapResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimLapsedRefundCategoryMapRequestDto requestDto
    ) {
        return ResponseEntity.ok(service.update(id, requestDto));
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<ClaimLapsedRefundCategoryMapResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =====================================================
    // GET BY RULE ID
    // =====================================================
    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<ClaimLapsedRefundCategoryMapResponseDto>> getByRuleId(
            @PathVariable Long ruleId
    ) {
        return ResponseEntity.ok(service.getByRuleId(ruleId));
    }

    // =====================================================
    // GET BY CATEGORY ID
    // =====================================================
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ClaimLapsedRefundCategoryMapResponseDto>> getByCategoryId(
            @PathVariable String categoryId
    ) {
        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }

    // =====================================================
    // DELETE
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}