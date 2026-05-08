package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.PostingStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/posting-status")
@RequiredArgsConstructor
public class PostingStatusMasterController {

    private final PostingStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<PostingStatusResponseDto>> create(
            @RequestBody PostingStatusRequestDto dto) {

        PostingStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<PostingStatusResponseDto>builder()
                        .success(true)
                        .message("Posting status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PostingStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PostingStatusRequestDto dto) {

        PostingStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<PostingStatusResponseDto>builder()
                        .success(true)
                        .message("Posting status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostingStatusResponseDto>> getById(
            @PathVariable Long id) {

        PostingStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<PostingStatusResponseDto>builder()
                        .success(true)
                        .message("Posting status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PostingStatusResponseDto>> getByCode(
            @PathVariable String code) {

        PostingStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<PostingStatusResponseDto>builder()
                        .success(true)
                        .message("Posting status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostingStatusResponseDto>>> getAll() {

        List<PostingStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<PostingStatusResponseDto>>builder()
                        .success(true)
                        .message("All posting statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PostingStatusResponseDto>>> getAllActive() {

        List<PostingStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<PostingStatusResponseDto>>builder()
                        .success(true)
                        .message("Active posting statuses fetched successfully")
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
                        .message("Posting status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}