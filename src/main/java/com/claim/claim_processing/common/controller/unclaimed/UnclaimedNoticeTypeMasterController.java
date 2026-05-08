package com.claim.claim_processing.common.controller.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedNoticeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedNoticeTypeResponseDto;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedNoticeTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unclaimed-notice-type")
@RequiredArgsConstructor
public class UnclaimedNoticeTypeMasterController {

    private final UnclaimedNoticeTypeMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<UnclaimedNoticeTypeResponseDto>> create(
            @RequestBody UnclaimedNoticeTypeRequestDto dto) {

        UnclaimedNoticeTypeResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedNoticeTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed notice type created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedNoticeTypeResponseDto>> update(
            @PathVariable Long id,
            @RequestBody UnclaimedNoticeTypeRequestDto dto) {

        UnclaimedNoticeTypeResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedNoticeTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed notice type updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedNoticeTypeResponseDto>> getById(
            @PathVariable Long id) {

        UnclaimedNoticeTypeResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedNoticeTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed notice type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<UnclaimedNoticeTypeResponseDto>> getByCode(
            @PathVariable String code) {

        UnclaimedNoticeTypeResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedNoticeTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed notice type fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<UnclaimedNoticeTypeResponseDto>>> getAll() {

        List<UnclaimedNoticeTypeResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedNoticeTypeResponseDto>>builder()
                        .success(true)
                        .message("All unclaimed notice types fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UnclaimedNoticeTypeResponseDto>>> getAllActive() {

        List<UnclaimedNoticeTypeResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedNoticeTypeResponseDto>>builder()
                        .success(true)
                        .message("Active unclaimed notice types fetched successfully")
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
                        .message("Unclaimed notice type deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}