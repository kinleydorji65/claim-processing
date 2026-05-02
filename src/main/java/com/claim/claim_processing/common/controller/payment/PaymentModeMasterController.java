package com.claim.claim_processing.common.controller.payment;

import com.claim.claim_processing.common.DTO.request.payment.PaymentModeRequestDto;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.service.payment.PaymentModeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/payment-mode")
@RequiredArgsConstructor
public class PaymentModeMasterController {

    private final PaymentModeMasterService service;

    // -------------------------
    // CREATE
    // -------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentModeResponseDto>> create(
            @RequestBody PaymentModeRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PaymentModeResponseDto>builder()
                        .success(true)
                        .message("Payment mode created successfully")
                        .data(service.create(requestDto))
                        .build()
        );
    }

    // -------------------------
    // UPDATE (FULL)
    // -------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentModeResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PaymentModeRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentModeResponseDto>builder()
                        .success(true)
                        .message("Payment mode updated successfully")
                        .data(service.update(id, requestDto))
                        .build()
        );
    }

    // -------------------------
    // PATCH (PARTIAL)
    // -------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentModeResponseDto>> patch(
            @PathVariable Long id,
            @RequestBody PaymentModeRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentModeResponseDto>builder()
                        .success(true)
                        .message("Payment mode updated successfully")
                        .data(service.patch(id, requestDto))
                        .build()
        );
    }

    // -------------------------
    // GET BY ID
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentModeResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentModeResponseDto>builder()
                        .success(true)
                        .message("Payment mode fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // -------------------------
    // GET BY CODE
    // -------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<PaymentModeResponseDto>> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentModeResponseDto>builder()
                        .success(true)
                        .message("Payment mode fetched successfully")
                        .data(service.getByCode(code))
                        .build()
        );
    }

    // -------------------------
    // GET ALL
    // -------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentModeResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.<List<PaymentModeResponseDto>>builder()
                        .success(true)
                        .message("Payment modes fetched successfully")
                        .data(service.getAll())
                        .build()
        );
    }

    // -------------------------
    // GET ALL ACTIVE
    // -------------------------
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PaymentModeResponseDto>>> getAllActive() {
        return ResponseEntity.ok(
                ApiResponse.<List<PaymentModeResponseDto>>builder()
                        .success(true)
                        .message("Active payment modes fetched successfully")
                        .data(service.getAllActive())
                        .build()
        );
    }

    // -------------------------
    // DELETE (SOFT DELETE)
    // -------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Payment mode deleted successfully")
                        .data(null)
                        .build()
        );
    }
}