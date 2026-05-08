package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ReversalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.ReversalStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.ReversalStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/reversal-status")
@RequiredArgsConstructor
public class ReversalStatusMasterController {

    private final ReversalStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<ReversalStatusResponseDto>> create(
            @RequestBody ReversalStatusRequestDto dto) {

        ReversalStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<ReversalStatusResponseDto>builder()
                        .success(true)
                        .message("Reversal status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ReversalStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ReversalStatusRequestDto dto) {

        ReversalStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<ReversalStatusResponseDto>builder()
                        .success(true)
                        .message("Reversal status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReversalStatusResponseDto>> getById(
            @PathVariable Long id) {

        ReversalStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ReversalStatusResponseDto>builder()
                        .success(true)
                        .message("Reversal status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ReversalStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ReversalStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<ReversalStatusResponseDto>builder()
                        .success(true)
                        .message("Reversal status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReversalStatusResponseDto>>> getAll() {

        List<ReversalStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ReversalStatusResponseDto>>builder()
                        .success(true)
                        .message("All reversal statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ReversalStatusResponseDto>>> getAllActive() {

        List<ReversalStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<ReversalStatusResponseDto>>builder()
                        .success(true)
                        .message("Active reversal statuses fetched successfully")
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
                        .message("Reversal status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}