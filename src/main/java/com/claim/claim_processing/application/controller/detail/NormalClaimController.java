package com.claim.claim_processing.application.controller.detail;

import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.service.detail.NormalClaimService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims/normal-details")
@RequiredArgsConstructor
public class NormalClaimController {

    private final NormalClaimService normalClaimService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<NormalClaimResponseDto>> create(
            @RequestBody NormalClaimRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(normalClaimService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<NormalClaimResponseDto>> update(
            @PathVariable Long id,
            @RequestBody NormalClaimRequestDto request
    ) {
        return ResponseEntity.ok(normalClaimService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<NormalClaimResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(normalClaimService.getById(id));
    }

    @GetMapping("/claim-application/{claimApplicationId}")
    public ResponseEntity<ApiResponseDTO<NormalClaimResponseDto>> getByClaimApplicationId(
            @PathVariable Long claimApplicationId
    ) {
        return ResponseEntity.ok(
                normalClaimService.getByClaimApplicationId(claimApplicationId)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<NormalClaimResponseDto>>> getAll() {
        return ResponseEntity.ok(normalClaimService.getAll());
    }
}