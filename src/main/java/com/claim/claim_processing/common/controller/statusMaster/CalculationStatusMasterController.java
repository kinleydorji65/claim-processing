package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.CalculationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.CalculationStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.CalculationStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/calculation-status")
@RequiredArgsConstructor
public class CalculationStatusMasterController {

    private final CalculationStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<CalculationStatusResponseDto>> create(
            @RequestBody CalculationStatusRequestDto dto) {

        CalculationStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<CalculationStatusResponseDto>builder()
                        .success(true)
                        .message("Calculation status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CalculationStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody CalculationStatusRequestDto dto) {

        CalculationStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<CalculationStatusResponseDto>builder()
                        .success(true)
                        .message("Calculation status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CalculationStatusResponseDto>> getById(
            @PathVariable Long id) {

        CalculationStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<CalculationStatusResponseDto>builder()
                        .success(true)
                        .message("Calculation status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CalculationStatusResponseDto>> getByCode(
            @PathVariable String code) {

        CalculationStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<CalculationStatusResponseDto>builder()
                        .success(true)
                        .message("Calculation status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<CalculationStatusResponseDto>>> getAll() {

        List<CalculationStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<CalculationStatusResponseDto>>builder()
                        .success(true)
                        .message("All calculation statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CalculationStatusResponseDto>>> getAllActive() {

        List<CalculationStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<CalculationStatusResponseDto>>builder()
                        .success(true)
                        .message("Active calculation statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Calculation status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}