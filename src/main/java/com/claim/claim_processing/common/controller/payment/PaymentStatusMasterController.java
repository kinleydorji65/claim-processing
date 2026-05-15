package com.claim.claim_processing.common.controller.payment;

import com.claim.claim_processing.common.DTO.request.payment.PaymentStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;
import com.claim.claim_processing.common.service.payment.PaymentStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/payment-status")
@RequiredArgsConstructor
public class PaymentStatusMasterController {

    private final PaymentStatusMasterService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PaymentStatusRequestDto requestDto) {

        ApiResponseDTO<PaymentStatusResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PaymentStatusRequestDto requestDto) {

        ApiResponseDTO<PaymentStatusResponseDto> response =
                service.update(id, requestDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<PaymentStatusResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<PaymentStatusResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<PaymentStatusResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<PaymentStatusResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}