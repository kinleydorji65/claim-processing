package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/claim-eligibilities")
@RequiredArgsConstructor
public class ClaimEligibilityController {

    private final ClaimEligibilityService claimEligibilityService;

    @GetMapping
    public ResponseEntity<List<ClaimEligibilityResponseDto>> getAllActive() {
        return ResponseEntity.ok(claimEligibilityService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimEligibilityResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(claimEligibilityService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ClaimEligibilityResponseDto> create(
            @RequestBody ClaimEligibilityCreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimEligibilityService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimEligibilityResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimEligibilityUpdateRequestDto requestDto) {
        return ResponseEntity.ok(claimEligibilityService.update(id, requestDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        claimEligibilityService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-circumstance/{id}")
    public ResponseEntity<List<ClaimEligibilityResponseDto>> getByCircumstance(@PathVariable Long id) {
        return ResponseEntity.ok(claimEligibilityService.getByClaimCircumstanceId(id));
    }

    @GetMapping("/by-scheme/{id}")
    public ResponseEntity<List<ClaimEligibilityResponseDto>> getByScheme(@PathVariable Long id) {
        return ResponseEntity.ok(claimEligibilityService.getBySchemeTypeId(id));
    }

    @GetMapping("/by-rule-type/{id}")
    public ResponseEntity<List<ClaimEligibilityResponseDto>> getByRuleType(@PathVariable Long id) {
        return ResponseEntity.ok(claimEligibilityService.getByRuleTypeId(id));
    }
}