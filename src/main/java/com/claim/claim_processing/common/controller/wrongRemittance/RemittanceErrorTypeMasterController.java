package com.claim.claim_processing.common.controller.wrongRemittance;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceErrorTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceErrorTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.service.wrongRemittance.RemittanceErrorTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/wrong-remittance-error-type")
@RequiredArgsConstructor
public class RemittanceErrorTypeMasterController {

    private final RemittanceErrorTypeMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RemittanceErrorTypeResponseDto>> create(
            @RequestBody RemittanceErrorTypeRequestDto dto) {

        ApiResponseDTO<RemittanceErrorTypeResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RemittanceErrorTypeResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RemittanceErrorTypeRequestDto dto) {

        ApiResponseDTO<RemittanceErrorTypeResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RemittanceErrorTypeResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<RemittanceErrorTypeResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<RemittanceErrorTypeResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<RemittanceErrorTypeResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RemittanceErrorTypeResponseDto>>> getAll() {

        ApiResponseDTO<List<RemittanceErrorTypeResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<RemittanceErrorTypeResponseDto>>> getAllActive() {

        ApiResponseDTO<List<RemittanceErrorTypeResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(
            @PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}