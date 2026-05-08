package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.TaxDepositStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.TaxDepositStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.TaxDepositStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/tax-deposit-status")
@RequiredArgsConstructor
public class TaxDepositStatusMasterController {

    private final TaxDepositStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<TaxDepositStatusResponseDto>> create(
            @RequestBody TaxDepositStatusRequestDto dto) {

        TaxDepositStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<TaxDepositStatusResponseDto>builder()
                        .success(true)
                        .message("Tax deposit status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxDepositStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody TaxDepositStatusRequestDto dto) {

        TaxDepositStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<TaxDepositStatusResponseDto>builder()
                        .success(true)
                        .message("Tax deposit status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxDepositStatusResponseDto>> getById(
            @PathVariable Long id) {

        TaxDepositStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<TaxDepositStatusResponseDto>builder()
                        .success(true)
                        .message("Tax deposit status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<TaxDepositStatusResponseDto>> getByCode(
            @PathVariable String code) {

        TaxDepositStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<TaxDepositStatusResponseDto>builder()
                        .success(true)
                        .message("Tax deposit status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaxDepositStatusResponseDto>>> getAll() {

        List<TaxDepositStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<TaxDepositStatusResponseDto>>builder()
                        .success(true)
                        .message("All tax deposit statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TaxDepositStatusResponseDto>>> getAllActive() {

        List<TaxDepositStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<TaxDepositStatusResponseDto>>builder()
                        .success(true)
                        .message("Active tax deposit statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Tax deposit status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}