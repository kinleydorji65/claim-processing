package com.claim.claim_processing.application.controller.detail;

import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/legal-recoveries")
@RequiredArgsConstructor
public class LegalRecoveryController {

    private final LegalRecoveryService legalRecoveryService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<LegalRecoveryResponseDto>> create(
            @RequestBody LegalRecoveryRequestDto request
    ) {
        return ResponseEntity.status(201).body(
                legalRecoveryService.create(request)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<LegalRecoveryResponseDto>> update(
            @PathVariable Long id,
            @RequestBody LegalRecoveryRequestDto request
    ) {
        return ResponseEntity.ok(
                legalRecoveryService.update(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<LegalRecoveryResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                legalRecoveryService.getById(id)
        );
    }

    @GetMapping("/claim-application/{claimApplicationId}")
    public ResponseEntity<ApiResponseDTO<LegalRecoveryResponseDto>> getByClaimApplicationId(
            @PathVariable Long claimApplicationId
    ) {
        return ResponseEntity.ok(
                legalRecoveryService.getByClaimApplicationId(claimApplicationId)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<LegalRecoveryResponseDto>>> getAll() {
        return ResponseEntity.ok(
                legalRecoveryService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                legalRecoveryService.delete(id)
        );
    }
}