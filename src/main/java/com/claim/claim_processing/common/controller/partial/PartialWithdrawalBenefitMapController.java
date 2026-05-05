package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalBenefitMapRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalBenefitMapResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalBenefitMapService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partial-withdrawal-benefit-map")
@RequiredArgsConstructor
public class PartialWithdrawalBenefitMapController {

    private final PartialWithdrawalBenefitMapService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PartialWithdrawalBenefitMapResponseDto>> create(
            @RequestBody PartialWithdrawalBenefitMapRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.<PartialWithdrawalBenefitMapResponseDto>builder()
                        .success(true)
                        .message("Created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PartialWithdrawalBenefitMapResponseDto>> update(
            @RequestBody PartialWithdrawalBenefitMapRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.<PartialWithdrawalBenefitMapResponseDto>builder()
                        .success(true)
                        .message("Updated successfully")
                        .data(service.update(dto))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartialWithdrawalBenefitMapResponseDto>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<PartialWithdrawalBenefitMapResponseDto>builder()
                        .success(true)
                        .message("Fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    @GetMapping("/accumulation/{accumulationId}")
    public ResponseEntity<ApiResponse<List<PartialWithdrawalBenefitMapResponseDto>>> getByAccumulationId(
            @PathVariable Long accumulationId) {

        return ResponseEntity.ok(
                ApiResponse.<List<PartialWithdrawalBenefitMapResponseDto>>builder()
                        .success(true)
                        .message("Fetched successfully")
                        .data(service.getByAccumulationId(accumulationId))
                        .build()
        );
    }

    @GetMapping("/benefit-component/{benefitComponentId}")
    public ResponseEntity<ApiResponse<List<PartialWithdrawalBenefitMapResponseDto>>> getByBenefitComponentId(
            @PathVariable Long benefitComponentId) {

        return ResponseEntity.ok(
                ApiResponse.<List<PartialWithdrawalBenefitMapResponseDto>>builder()
                        .success(true)
                        .message("Fetched successfully")
                        .data(service.getByBenefitComponentId(benefitComponentId))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Deleted successfully")
                        .data(null)
                        .build()
        );
    }
}