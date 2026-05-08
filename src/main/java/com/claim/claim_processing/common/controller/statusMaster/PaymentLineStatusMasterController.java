package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PaymentLineStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.PaymentLineStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.PaymentLineStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/payment-line-status")
@RequiredArgsConstructor
public class PaymentLineStatusMasterController {

    private final PaymentLineStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentLineStatusResponseDto>> create(
            @RequestBody PaymentLineStatusRequestDto dto) {

        PaymentLineStatusResponseDto response = service.create(dto);

        return ResponseEntity.ok(
                ApiResponse.<PaymentLineStatusResponseDto>builder()
                        .success(true)
                        .message("Payment line status created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= UPDATE =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentLineStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PaymentLineStatusRequestDto dto) {

        PaymentLineStatusResponseDto response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<PaymentLineStatusResponseDto>builder()
                        .success(true)
                        .message("Payment line status updated successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentLineStatusResponseDto>> getById(
            @PathVariable Long id) {

        PaymentLineStatusResponseDto response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<PaymentLineStatusResponseDto>builder()
                        .success(true)
                        .message("Payment line status fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PaymentLineStatusResponseDto>> getByCode(
            @PathVariable String code) {

        PaymentLineStatusResponseDto response = service.getByCode(code);

        return ResponseEntity.ok(
                ApiResponse.<PaymentLineStatusResponseDto>builder()
                        .success(true)
                        .message("Payment line status fetched by code successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentLineStatusResponseDto>>> getAll() {

        List<PaymentLineStatusResponseDto> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<PaymentLineStatusResponseDto>>builder()
                        .success(true)
                        .message("All payment line statuses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PaymentLineStatusResponseDto>>> getAllActive() {

        List<PaymentLineStatusResponseDto> response = service.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<PaymentLineStatusResponseDto>>builder()
                        .success(true)
                        .message("Active payment line statuses fetched successfully")
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
                        .message("Payment line status deleted successfully")
                        .data("Deleted ID: " + id)
                        .build()
        );
    }
}