package com.claim.claim_processing.common.controller.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentDetailRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentDetailResponseDto;
import com.claim.claim_processing.common.service.contribution.BenefitComponentTypeDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benefit-component-details")
@RequiredArgsConstructor
public class BenefitComponentTypeDetailController {

    private final BenefitComponentTypeDetailService service;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<BenefitComponentDetailResponseDto>> create(
            @RequestBody BenefitComponentDetailRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.<BenefitComponentDetailResponseDto>builder()
                        .success(true)
                        .message("Created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // =========================
    // UPDATE (PATCH - PARTIAL)
    // =========================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BenefitComponentDetailResponseDto>> update(
            @PathVariable Long id,
            @RequestBody BenefitComponentDetailRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.<BenefitComponentDetailResponseDto>builder()
                        .success(true)
                        .message("Updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BenefitComponentDetailResponseDto>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<BenefitComponentDetailResponseDto>builder()
                        .success(true)
                        .message("Fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // =========================
    // GET ALL ACTIVE
    // =========================
    @GetMapping
    public ResponseEntity<ApiResponse<List<BenefitComponentDetailResponseDto>>> getAllActive() {

        return ResponseEntity.ok(
                ApiResponse.<List<BenefitComponentDetailResponseDto>>builder()
                        .success(true)
                        .message("Fetched all active records")
                        .data(service.getAllActive())
                        .build()
        );
    }

    // =========================
    // FILTER BY BENEFIT TYPE
    // =========================
    @GetMapping("/by-benefit-type/{id}")
    public ResponseEntity<ApiResponse<List<BenefitComponentDetailResponseDto>>> getByBenefitType(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<List<BenefitComponentDetailResponseDto>>builder()
                        .success(true)
                        .message("Fetched by benefit component type")
                        .data(service.getByBenefitComponentTypeId(id))
                        .build()
        );
    }

    // =========================
    // FILTER BY COMPONENT
    // =========================
    @GetMapping("/by-component/{id}")
    public ResponseEntity<ApiResponse<List<BenefitComponentDetailResponseDto>>> getByComponent(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<List<BenefitComponentDetailResponseDto>>builder()
                        .success(true)
                        .message("Fetched by component")
                        .data(service.getByComponentId(id))
                        .build()
        );
    }

    // =========================
    // DELETE
    // =========================
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