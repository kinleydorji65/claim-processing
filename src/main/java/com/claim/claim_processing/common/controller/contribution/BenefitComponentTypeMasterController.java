package com.claim.claim_processing.common.controller.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.service.contribution.BenefitComponentTypeMasterService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> create(
            @RequestBody BenefitComponentTypeMasterRequestDto requestDto
    ) {
        ApiResponseDTO<BenefitComponentTypeMasterResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Update existing record
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody BenefitComponentTypeMasterRequestDto requestDto
    ) {
        ApiResponseDTO<BenefitComponentTypeMasterResponseDto> response = service.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Get by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {
        ApiResponseDTO<BenefitComponentTypeMasterResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all records
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Get by active status
     * Example:
     * /api/benefit-component-types/status/Y
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(
            @PathVariable ActivityEnum status
    ) {
        ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> response = service.getByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Search by name
     * Example:
     * /api/benefit-component-types/search?keyword=pension
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchByName(
            @RequestParam String keyword
    ) {
        ApiResponseDTO<List<BenefitComponentTypeMasterResponseDto>> response = service.searchByName(keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft delete (set inactive)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}