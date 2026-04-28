package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundComponentMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundComponentMapResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundComponentMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/claims/lapsed-refund-component-map")
@RequiredArgsConstructor
public class ClaimLapsedRefundComponentMapController {

    private final ClaimLapsedRefundComponentMapService service;

    // ---------------------------------------------------
    // CREATE
    // ---------------------------------------------------
    @PostMapping
    public ResponseEntity<ClaimLapsedRefundComponentMapResponseDto> create(
            @RequestBody ClaimLapsedRefundComponentMapRequestDto dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    // ---------------------------------------------------
    // UPDATE
    // ---------------------------------------------------
    @PutMapping
    public ResponseEntity<ClaimLapsedRefundComponentMapResponseDto> update(
            @RequestBody ClaimLapsedRefundComponentMapRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(dto));
    }

    // ---------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ClaimLapsedRefundComponentMapResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ---------------------------------------------------
    // GET BY RULE ID
    // ---------------------------------------------------
    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<ClaimLapsedRefundComponentMapResponseDto>> getByRuleId(
            @PathVariable Long ruleId
    ) {
        return ResponseEntity.ok(service.getByRuleId(ruleId));
    }

    // ---------------------------------------------------
    // GET BY BENEFIT COMPONENT TYPE ID
    // ---------------------------------------------------
    @GetMapping("/benefit-component/{benefitComponentTypeId}")
    public ResponseEntity<List<ClaimLapsedRefundComponentMapResponseDto>> getByBenefitComponentTypeId(
            @PathVariable Long benefitComponentTypeId
    ) {
        return ResponseEntity.ok(service.getByBenefitComponentTypeId(benefitComponentTypeId));
    }

    // ---------------------------------------------------
    // DELETE
    // ---------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}