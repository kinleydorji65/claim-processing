package com.claim.claim_processing.common.controller.claim;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.ClaimCircumstanceService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/claim/masters/claim-circumstances")
@RequiredArgsConstructor
public class ClaimCircumstanceController {

    private final ClaimCircumstanceService claimCircumstanceService;

    /**
     * Get all active claim circumstances
     * GET /api/claim/masters/claim-circumstances
     */
    @GetMapping
    public ResponseEntity<List<ClaimCircumstanceResponseDto>> getAllActive() {
        log.info("REST request to get all active claim circumstances");
        List<ClaimCircumstanceResponseDto> response = claimCircumstanceService.getAllActive();
        return ResponseEntity.ok(response);
    }

    /**
     * Get claim circumstance by ID
     * GET /api/claim/masters/claim-circumstances/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClaimCircumstanceResponseDto> getById(@PathVariable Long id) {
        log.info("REST request to get claim circumstance by id: {}", id);
        ClaimCircumstanceResponseDto response = claimCircumstanceService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Create new claim circumstance
     * POST /api/claim/masters/claim-circumstances
     */
    @PostMapping
    public ResponseEntity<ClaimCircumstanceResponseDto> create(
            @Valid @RequestBody ClaimCircumstanceCreateRequestDto requestDto) {
        log.info("REST request to create claim circumstance: {}", requestDto);
        ClaimCircumstanceResponseDto response = claimCircumstanceService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update existing claim circumstance
     * PUT /api/claim/masters/claim-circumstances/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClaimCircumstanceResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ClaimCircumstanceUpdateRequestDto requestDto) {
        log.info("REST request to update claim circumstance: {} with id: {}", requestDto, id);
        ClaimCircumstanceResponseDto response = claimCircumstanceService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate claim circumstance (soft delete)
     * PATCH /api/claim/masters/claim-circumstances/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        log.info("REST request to deactivate claim circumstance with id: {}", id);
        claimCircumstanceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Alternative endpoint for deactivation using DELETE method
     * DELETE /api/claim/masters/claim-circumstances/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("REST request to delete/deactivate claim circumstance with id: {}", id);
        claimCircumstanceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
