package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.CalculationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.CalculationStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.CalculationStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/calculation-status")
@RequiredArgsConstructor
public class CalculationStatusMasterController {

    private final CalculationStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<CalculationStatusResponseDto>> create(
            @RequestBody CalculationStatusRequestDto dto) {

        ApiResponseDTO<CalculationStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CalculationStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody CalculationStatusRequestDto dto) {

        ApiResponseDTO<CalculationStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CalculationStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<CalculationStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<CalculationStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<CalculationStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CalculationStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<CalculationStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<CalculationStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<CalculationStatusResponseDto>> response = service.getAllActive();

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