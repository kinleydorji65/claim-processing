package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PaymentLineStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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
    public ResponseEntity<ApiResponseDTO<PaymentLineStatusResponseDto>> create(
            @RequestBody PaymentLineStatusRequestDto dto) {

        ApiResponseDTO<PaymentLineStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PaymentLineStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody PaymentLineStatusRequestDto dto) {

        ApiResponseDTO<PaymentLineStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PaymentLineStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<PaymentLineStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<PaymentLineStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<PaymentLineStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PaymentLineStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<PaymentLineStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<PaymentLineStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<PaymentLineStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}