package com.claim.claim_processing.common.controller.unclaimed;

import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedTypeResponseDto;
import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.service.unclaimed.UnclaimedTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/unclaimed-type")
@RequiredArgsConstructor
public class UnclaimedTypeMasterController {

    private final UnclaimedTypeMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<UnclaimedTypeResponseDto>> create(
            @RequestBody UnclaimedTypeRequestDto dto) {

        UnclaimedTypeResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed type created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedTypeResponseDto>> update(
            @PathVariable Long id,
            @RequestBody UnclaimedTypeRequestDto dto) {

        UnclaimedTypeResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed type updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnclaimedTypeResponseDto>> getById(
            @PathVariable Long id) {

        UnclaimedTypeResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<UnclaimedTypeResponseDto>> getByCode(
            @PathVariable String code) {

        UnclaimedTypeResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<UnclaimedTypeResponseDto>builder()
                        .success(true)
                        .message("Unclaimed type fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<UnclaimedTypeResponseDto>>> getAll() {

        List<UnclaimedTypeResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedTypeResponseDto>>builder()
                        .success(true)
                        .message("All unclaimed types fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UnclaimedTypeResponseDto>>> getAllActive() {

        List<UnclaimedTypeResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<UnclaimedTypeResponseDto>>builder()
                        .success(true)
                        .message("Active unclaimed types fetched successfully")
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
                        .message("Unclaimed type deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}