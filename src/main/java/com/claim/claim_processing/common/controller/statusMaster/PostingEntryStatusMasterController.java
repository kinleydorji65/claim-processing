package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingEntryStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingEntryStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.PostingEntryStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/posting-entry-status")
@RequiredArgsConstructor
public class PostingEntryStatusMasterController {

    private final PostingEntryStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<PostingEntryStatusResponseDto>> create(
            @RequestBody PostingEntryStatusRequestDto dto) {

        PostingEntryStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<PostingEntryStatusResponseDto>builder()
                        .success(true)
                        .message("Posting entry status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PostingEntryStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PostingEntryStatusRequestDto dto) {

        PostingEntryStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<PostingEntryStatusResponseDto>builder()
                        .success(true)
                        .message("Posting entry status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostingEntryStatusResponseDto>> getById(
            @PathVariable Long id) {

        PostingEntryStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<PostingEntryStatusResponseDto>builder()
                        .success(true)
                        .message("Posting entry status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PostingEntryStatusResponseDto>> getByCode(
            @PathVariable String code) {

        PostingEntryStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<PostingEntryStatusResponseDto>builder()
                        .success(true)
                        .message("Posting entry status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostingEntryStatusResponseDto>>> getAll() {

        List<PostingEntryStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<PostingEntryStatusResponseDto>>builder()
                        .success(true)
                        .message("All posting entry statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PostingEntryStatusResponseDto>>> getAllActive() {

        List<PostingEntryStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<PostingEntryStatusResponseDto>>builder()
                        .success(true)
                        .message("Active posting entry statuses fetched successfully")
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
                        .message("Posting entry status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}