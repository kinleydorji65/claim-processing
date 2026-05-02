package com.claim.claim_processing.common.controller.specialCase;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseRefundReasonResponseDto;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseRefundReasonMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/special-case-refund-reason")
@RequiredArgsConstructor
public class SpecialCaseRefundReasonMasterController {

    private final SpecialCaseRefundReasonMasterService service;

    // -------------------------
    // CREATE
    // -------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<SpecialCaseRefundReasonResponseDto>> create(
            @RequestBody SpecialCaseRefundReasonRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SpecialCaseRefundReasonResponseDto>builder()
                        .success(true)
                        .message("Special case refund reason created successfully")
                        .data(service.create(requestDto))
                        .build()
        );
    }

    // -------------------------
    // UPDATE (FULL)
    // -------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialCaseRefundReasonResponseDto>> update(
            @PathVariable Long id,
            @RequestBody SpecialCaseRefundReasonRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                ApiResponse.<SpecialCaseRefundReasonResponseDto>builder()
                        .success(true)
                        .message("Special case refund reason updated successfully")
                        .data(service.update(id, requestDto))
                        .build()
        );
    }

    // -------------------------
    // PATCH (PARTIAL)
    // -------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialCaseRefundReasonResponseDto>> patch(
            @PathVariable Long id,
            @RequestBody SpecialCaseRefundReasonRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                ApiResponse.<SpecialCaseRefundReasonResponseDto>builder()
                        .success(true)
                        .message("Special case refund reason partially updated successfully")
                        .data(service.patch(id, requestDto))
                        .build()
        );
    }

    // -------------------------
    // GET BY ID
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialCaseRefundReasonResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.<SpecialCaseRefundReasonResponseDto>builder()
                        .success(true)
                        .message("Special case refund reason fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // -------------------------
    // GET BY CODE
    // -------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<SpecialCaseRefundReasonResponseDto>> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(
                ApiResponse.<SpecialCaseRefundReasonResponseDto>builder()
                        .success(true)
                        .message("Special case refund reason fetched successfully")
                        .data(service.getByCode(code))
                        .build()
        );
    }

    // -------------------------
    // GET ALL
    // -------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialCaseRefundReasonResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.<List<SpecialCaseRefundReasonResponseDto>>builder()
                        .success(true)
                        .message("Special case refund reasons fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    // -------------------------
    // GET ALL ACTIVE
    // -------------------------
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SpecialCaseRefundReasonResponseDto>>> getAllActive() {
        return ResponseEntity.ok(
                ApiResponse.<List<SpecialCaseRefundReasonResponseDto>>builder()
                        .success(true)
                        .message("Active special case refund reasons fetched successfully")
                        .data(service.getAllActive())
                        .build()
        );
    }

    // -------------------------
    // SOFT DELETE
    // -------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Special case refund reason deleted successfully")
                        .data(null)
                        .build()
        );
    }
}