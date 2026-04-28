package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityComponentMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityComponentMapResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityComponentMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/claim-eligibility-component-map")
@RequiredArgsConstructor
public class ClaimEligibilityComponentMapController {

    private final ClaimEligibilityComponentMapService service;

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @PostMapping
    public ResponseEntity<ClaimEligibilityComponentMapResponseDto> create(
            @RequestBody ClaimEligibilityComponentMapRequestDto dto) {

        return ResponseEntity.status(201).body(service.create(dto));
    }

    // -------------------------------------------------
    // UPDATE
    // -------------------------------------------------
    @PutMapping
    public ResponseEntity<ClaimEligibilityComponentMapResponseDto> update(
            @RequestBody ClaimEligibilityComponentMapRequestDto dto) {

        return ResponseEntity.ok(service.update(dto));
    }

    // -------------------------------------------------
    // GET BY ID
    // -------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ClaimEligibilityComponentMapResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // -------------------------------------------------
    // GET ALL
    // -------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ClaimEligibilityComponentMapResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // -------------------------------------------------
    // GET BY RULE ID
    // -------------------------------------------------
    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<ClaimEligibilityComponentMapResponseDto>> getByRuleId(
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

    @GetMapping("/benefit/{benefitComponentTypeId}")
    public ResponseEntity<List<ClaimEligibilityComponentMapResponseDto>> getByBenefitComponentTypeId(
            @PathVariable Long benefitComponentTypeId) {

        return ResponseEntity.ok(
                service.getByBenefitComponentTypeId(benefitComponentTypeId)
        );
    }
}