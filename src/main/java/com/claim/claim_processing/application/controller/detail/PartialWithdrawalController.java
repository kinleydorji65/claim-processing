package com.claim.claim_processing.application.controller.detail;

import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.service.detail.PartialWithdrawalService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/partial-withdrawals")
@RequiredArgsConstructor
public class PartialWithdrawalController {

    private final PartialWithdrawalService partialWithdrawalService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<PartialWithdrawalResponseDto>> create(
            @RequestBody PartialWithdrawalRequestDto request
    ) {
        return ResponseEntity.status(201).body(
                partialWithdrawalService.create(request)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PartialWithdrawalResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalRequestDto request
    ) {
        return ResponseEntity.ok(
                partialWithdrawalService.update(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PartialWithdrawalResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                partialWithdrawalService.getById(id)
        );
    }

    @GetMapping("/claim-application/{claimApplicationId}")
    public ResponseEntity<ApiResponseDTO<PartialWithdrawalResponseDto>> getByClaimApplicationId(
            @PathVariable Long claimApplicationId
    ) {
        return ResponseEntity.ok(
                partialWithdrawalService.getByClaimApplicationId(claimApplicationId)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PartialWithdrawalResponseDto>>> getAll() {
        return ResponseEntity.ok(
                partialWithdrawalService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                partialWithdrawalService.delete(id)
        );
    }
}