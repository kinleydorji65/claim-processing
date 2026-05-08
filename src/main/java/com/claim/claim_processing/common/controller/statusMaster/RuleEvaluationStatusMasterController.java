package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RuleEvaluationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.RuleEvaluationStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.RuleEvaluationStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/rule-evaluation-status")
@RequiredArgsConstructor
public class RuleEvaluationStatusMasterController {

    private final RuleEvaluationStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<RuleEvaluationStatusResponseDto>> create(
            @RequestBody RuleEvaluationStatusRequestDto dto) {

        RuleEvaluationStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<RuleEvaluationStatusResponseDto>builder()
                        .success(true)
                        .message("Rule evaluation status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RuleEvaluationStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RuleEvaluationStatusRequestDto dto) {

        RuleEvaluationStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<RuleEvaluationStatusResponseDto>builder()
                        .success(true)
                        .message("Rule evaluation status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RuleEvaluationStatusResponseDto>> getById(
            @PathVariable Long id) {

        RuleEvaluationStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<RuleEvaluationStatusResponseDto>builder()
                        .success(true)
                        .message("Rule evaluation status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<RuleEvaluationStatusResponseDto>> getByCode(
            @PathVariable String code) {

        RuleEvaluationStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<RuleEvaluationStatusResponseDto>builder()
                        .success(true)
                        .message("Rule evaluation status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<RuleEvaluationStatusResponseDto>>> getAll() {

        List<RuleEvaluationStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<RuleEvaluationStatusResponseDto>>builder()
                        .success(true)
                        .message("All rule evaluation statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RuleEvaluationStatusResponseDto>>> getAllActive() {

        List<RuleEvaluationStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<RuleEvaluationStatusResponseDto>>builder()
                        .success(true)
                        .message("Active rule evaluation statuses fetched successfully")
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
                        .message("Rule evaluation status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}