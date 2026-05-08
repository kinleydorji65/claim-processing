package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.FinalPayableReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.FinalPayableReviewStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.FinalPayableReviewStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/final-payable-review-status")
@RequiredArgsConstructor
public class FinalPayableReviewStatusMasterController {

    private final FinalPayableReviewStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<FinalPayableReviewStatusResponseDto>> create(
            @RequestBody FinalPayableReviewStatusRequestDto dto
    ) {

        FinalPayableReviewStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<FinalPayableReviewStatusResponseDto>builder()
                        .success(true)
                        .message("Final payable review status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FinalPayableReviewStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody FinalPayableReviewStatusRequestDto dto
    ) {

        FinalPayableReviewStatusResponseDto response =
                service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<FinalPayableReviewStatusResponseDto>builder()
                        .success(true)
                        .message("Final payable review status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FinalPayableReviewStatusResponseDto>> getById(
            @PathVariable Long id
    ) {

        FinalPayableReviewStatusResponseDto response =
                service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<FinalPayableReviewStatusResponseDto>builder()
                        .success(true)
                        .message("Final payable review status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<FinalPayableReviewStatusResponseDto>> getByCode(
            @PathVariable String code
    ) {

        FinalPayableReviewStatusResponseDto response =
                service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<FinalPayableReviewStatusResponseDto>builder()
                        .success(true)
                        .message("Final payable review status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<FinalPayableReviewStatusResponseDto>>> getAll() {

        List<FinalPayableReviewStatusResponseDto> response =
                service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<FinalPayableReviewStatusResponseDto>>builder()
                        .success(true)
                        .message("All final payable review statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<FinalPayableReviewStatusResponseDto>>> getAllActive() {

        List<FinalPayableReviewStatusResponseDto> response =
                service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<FinalPayableReviewStatusResponseDto>>builder()
                        .success(true)
                        .message("Active final payable review statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Final payable review status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}