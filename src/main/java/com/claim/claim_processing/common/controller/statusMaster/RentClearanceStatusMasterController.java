package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RentClearanceStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.RentClearanceStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.RentClearanceStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/rent-clearance-status")
@RequiredArgsConstructor
public class RentClearanceStatusMasterController {

    private final RentClearanceStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<RentClearanceStatusResponseDto>> create(
            @RequestBody RentClearanceStatusRequestDto dto) {

        RentClearanceStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<RentClearanceStatusResponseDto>builder()
                        .success(true)
                        .message("Rent clearance status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RentClearanceStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RentClearanceStatusRequestDto dto) {

        RentClearanceStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<RentClearanceStatusResponseDto>builder()
                        .success(true)
                        .message("Rent clearance status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentClearanceStatusResponseDto>> getById(
            @PathVariable Long id) {

        RentClearanceStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<RentClearanceStatusResponseDto>builder()
                        .success(true)
                        .message("Rent clearance status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<RentClearanceStatusResponseDto>> getByCode(
            @PathVariable String code) {

        RentClearanceStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<RentClearanceStatusResponseDto>builder()
                        .success(true)
                        .message("Rent clearance status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<RentClearanceStatusResponseDto>>> getAll() {

        List<RentClearanceStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<RentClearanceStatusResponseDto>>builder()
                        .success(true)
                        .message("All rent clearance statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RentClearanceStatusResponseDto>>> getAllActive() {

        List<RentClearanceStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<RentClearanceStatusResponseDto>>builder()
                        .success(true)
                        .message("Active rent clearance statuses fetched successfully")
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
                        .message("Rent clearance status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}