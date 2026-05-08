package com.claim.claim_processing.common.controller.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedStatusResponseDto;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/unclaimed-status")
@RequiredArgsConstructor
public class UnclaimedStatusMasterController {

    private final UnclaimedStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<UnclaimedStatusResponseDto>> create(
            @RequestBody UnclaimedStatusRequestDto dto) {

        UnclaimedStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedStatusResponseDto>builder()
                        .success(true)
                        .message("Unclaimed status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody UnclaimedStatusRequestDto dto) {

        UnclaimedStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedStatusResponseDto>builder()
                        .success(true)
                        .message("Unclaimed status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedStatusResponseDto>> getById(
            @PathVariable Long id) {

        UnclaimedStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedStatusResponseDto>builder()
                        .success(true)
                        .message("Unclaimed status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<UnclaimedStatusResponseDto>> getByCode(
            @PathVariable String code) {

        UnclaimedStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedStatusResponseDto>builder()
                        .success(true)
                        .message("Unclaimed status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<UnclaimedStatusResponseDto>>> getAll() {

        List<UnclaimedStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedStatusResponseDto>>builder()
                        .success(true)
                        .message("All unclaimed statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UnclaimedStatusResponseDto>>> getAllActive() {

        List<UnclaimedStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedStatusResponseDto>>builder()
                        .success(true)
                        .message("Active unclaimed statuses fetched successfully")
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
                        .message("Unclaimed status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}