package com.claim.claim_processing.common.controller.wrongRemittance;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceErrorTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceErrorTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.service.wrongRemittance.RemittanceErrorTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/wrong-remittance-error-type")
@RequiredArgsConstructor
public class RemittanceErrorTypeMasterController {

    private final RemittanceErrorTypeMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<RemittanceErrorTypeResponseDto>> create(
            @RequestBody RemittanceErrorTypeRequestDto dto) {

        RemittanceErrorTypeResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<RemittanceErrorTypeResponseDto>builder()
                        .success(true)
                        .message("Wrong remittance error type created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RemittanceErrorTypeResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RemittanceErrorTypeRequestDto dto) {

        RemittanceErrorTypeResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<RemittanceErrorTypeResponseDto>builder()
                        .success(true)
                        .message("Wrong remittance error type updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RemittanceErrorTypeResponseDto>> getById(
            @PathVariable Long id) {

        RemittanceErrorTypeResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<RemittanceErrorTypeResponseDto>builder()
                        .success(true)
                        .message("Wrong remittance error type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<RemittanceErrorTypeResponseDto>> getByCode(
            @PathVariable String code) {

        RemittanceErrorTypeResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<RemittanceErrorTypeResponseDto>builder()
                        .success(true)
                        .message("Wrong remittance error type fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<RemittanceErrorTypeResponseDto>>> getAll() {

        List<RemittanceErrorTypeResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<RemittanceErrorTypeResponseDto>>builder()
                        .success(true)
                        .message("All wrong remittance error types fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RemittanceErrorTypeResponseDto>>> getAllActive() {

        List<RemittanceErrorTypeResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<RemittanceErrorTypeResponseDto>>builder()
                        .success(true)
                        .message("Active wrong remittance error types fetched successfully")
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
                        .message("Wrong remittance error type deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}