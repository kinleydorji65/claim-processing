package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.TaxDepositStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.TaxDepositStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.TaxDepositStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/tax-deposit-status")
@RequiredArgsConstructor
public class TaxDepositStatusMasterController {

    private final TaxDepositStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<TaxDepositStatusResponseDto>> create(
            @RequestBody TaxDepositStatusRequestDto dto) {

        ApiResponseDTO<TaxDepositStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TaxDepositStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody TaxDepositStatusRequestDto dto) {

        ApiResponseDTO<TaxDepositStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TaxDepositStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<TaxDepositStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<TaxDepositStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<TaxDepositStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TaxDepositStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<TaxDepositStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<TaxDepositStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<TaxDepositStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}