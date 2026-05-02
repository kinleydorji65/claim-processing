package com.claim.claim_processing.common.controller.payment;

import com.claim.claim_processing.common.DTO.request.payment.PaymentStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;
import com.claim.claim_processing.common.service.payment.PaymentStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/payment-status")
@RequiredArgsConstructor
public class PaymentStatusMasterController {

    private final PaymentStatusMasterService service;

    // -------------------------
    // CREATE
    // -------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentStatusResponseDto>> create(
            @RequestBody PaymentStatusRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PaymentStatusResponseDto>builder()
                        .success(true)
                        .message("Payment status created successfully")
                        .data(service.create(requestDto))
                        .build()
        );
    }

    // -------------------------
    // UPDATE (FULL)
    // -------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PaymentStatusRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentStatusResponseDto>builder()
                        .success(true)
                        .message("Payment status updated successfully")
                        .data(service.update(id, requestDto))
                        .build()
        );
    }

    // -------------------------
    // GET BY ID
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentStatusResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentStatusResponseDto>builder()
                        .success(true)
                        .message("Payment status fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // -------------------------
    // GET BY CODE
    // -------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PaymentStatusResponseDto>> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentStatusResponseDto>builder()
                        .success(true)
                        .message("Payment status fetched successfully")
                        .data(service.getByCode(code))
                        .build()
        );
    }

    // -------------------------
    // GET ALL
    // -------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentStatusResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.<List<PaymentStatusResponseDto>>builder()
                        .success(true)
                        .message("Payment statuses fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    // -------------------------
    // GET ALL ACTIVE
    // -------------------------
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PaymentStatusResponseDto>>> getAllActive() {
        return ResponseEntity.ok(
                ApiResponse.<List<PaymentStatusResponseDto>>builder()
                        .success(true)
                        .message("Active payment statuses fetched successfully")
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
                        .message("Payment status deleted successfully")
                        .data(null)
                        .build()
        );
    }
}