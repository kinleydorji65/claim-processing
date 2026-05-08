package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.VerificationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.VerificationStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.VerificationStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/verification-status")
@RequiredArgsConstructor
public class VerificationStatusMasterController {

    private final VerificationStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<VerificationStatusResponseDto>> create(
            @RequestBody VerificationStatusRequestDto dto) {

        VerificationStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<VerificationStatusResponseDto>builder()
                        .success(true)
                        .message("Verification status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<VerificationStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody VerificationStatusRequestDto dto) {

        VerificationStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<VerificationStatusResponseDto>builder()
                        .success(true)
                        .message("Verification status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VerificationStatusResponseDto>> getById(
            @PathVariable Long id) {

        VerificationStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<VerificationStatusResponseDto>builder()
                        .success(true)
                        .message("Verification status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<VerificationStatusResponseDto>> getByCode(
            @PathVariable String code) {

        VerificationStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<VerificationStatusResponseDto>builder()
                        .success(true)
                        .message("Verification status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<VerificationStatusResponseDto>>> getAll() {

        List<VerificationStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<VerificationStatusResponseDto>>builder()
                        .success(true)
                        .message("All verification statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VerificationStatusResponseDto>>> getAllActive() {

        List<VerificationStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<VerificationStatusResponseDto>>builder()
                        .success(true)
                        .message("Active verification statuses fetched successfully")
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
                        .message("Verification status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}