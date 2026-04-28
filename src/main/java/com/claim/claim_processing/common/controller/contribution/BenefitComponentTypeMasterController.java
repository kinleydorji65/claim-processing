package com.claim.claim_processing.common.controller.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.service.contribution.BenefitComponentTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benefit-component-types")
@RequiredArgsConstructor
public class BenefitComponentTypeMasterController {

    private final BenefitComponentTypeMasterService service;

    /**
     * Create new record
     */
    @PostMapping
    public ResponseEntity<BenefitComponentTypeMasterResponseDto> create(
            @RequestBody BenefitComponentTypeMasterRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(requestDto));
    }

    /**
     * Update existing record
     */
    @PutMapping("/{id}")
    public ResponseEntity<BenefitComponentTypeMasterResponseDto> update(
            @PathVariable Long id,
            @RequestBody BenefitComponentTypeMasterRequestDto requestDto
    ) {
        return ResponseEntity.ok(service.update(id, requestDto));
    }

    /**
     * Get by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<BenefitComponentTypeMasterResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Get all records
     */
    @GetMapping
    public ResponseEntity<List<BenefitComponentTypeMasterResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Get by active status
     * Example:
     * /api/benefit-component-types/status/Y
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BenefitComponentTypeMasterResponseDto>> getByStatus(
            @PathVariable ActivityEnum status
    ) {
        return ResponseEntity.ok(service.getByStatus(status));
    }

    /**
     * Search by name
     * Example:
     * /api/benefit-component-types/search?keyword=pension
     */
    @GetMapping("/search")
    public ResponseEntity<List<BenefitComponentTypeMasterResponseDto>> searchByName(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(service.searchByName(keyword));
    }

    /**
     * Soft delete (set inactive)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Record deactivated successfully.");
    }
}