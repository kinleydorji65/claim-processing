package com.claim.claim_processing.common.controller.payment;

import com.claim.claim_processing.common.DTO.request.payment.PaymentModeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.service.payment.PaymentModeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/payment-mode")
@RequiredArgsConstructor
public class PaymentModeMasterController {

    private final PaymentModeMasterService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PaymentModeRequestDto requestDto) {

        ApiResponseDTO<PaymentModeResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PaymentModeRequestDto requestDto) {

        ApiResponseDTO<PaymentModeResponseDto> response = service.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<PaymentModeResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<PaymentModeResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<PaymentModeResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<PaymentModeResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}