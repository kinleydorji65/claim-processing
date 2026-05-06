package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ApprovalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.ApprovalStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.ApprovalStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/approval-status")
@RequiredArgsConstructor
public class ApprovalStatusMasterController {

    private final ApprovalStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<ApprovalStatusResponseDto>> create(
            @RequestBody ApprovalStatusRequestDto dto) {

        ApprovalStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<ApprovalStatusResponseDto>builder()
                        .success(true)
                        .message("Approval status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ApprovalStatusRequestDto dto) {

        ApprovalStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<ApprovalStatusResponseDto>builder()
                        .success(true)
                        .message("Approval status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApprovalStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ApprovalStatusResponseDto>builder()
                        .success(true)
                        .message("Fetched approval status successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ApprovalStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApprovalStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<ApprovalStatusResponseDto>builder()
                        .success(true)
                        .message("Fetched approval status by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApprovalStatusResponseDto>>> getAll() {

        List<ApprovalStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ApprovalStatusResponseDto>>builder()
                        .success(true)
                        .message("Fetched all approval statuses successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ApprovalStatusResponseDto>>> getAllActive() {

        List<ApprovalStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<ApprovalStatusResponseDto>>builder()
                        .success(true)
                        .message("Fetched active approval statuses successfully")
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
                        .message("Approval status deleted successfully")
                        .data("ID: " + id)
                        .build()
        );
    }
}