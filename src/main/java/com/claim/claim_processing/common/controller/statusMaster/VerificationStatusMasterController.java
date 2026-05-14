package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.VerificationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.VerificationStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.VerificationStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/verification-status")
@RequiredArgsConstructor
public class VerificationStatusMasterController {

    private final VerificationStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<VerificationStatusResponseDto>> create(
            @RequestBody VerificationStatusRequestDto dto) {

        ApiResponseDTO<VerificationStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<VerificationStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody VerificationStatusRequestDto dto) {

        ApiResponseDTO<VerificationStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<VerificationStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<VerificationStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<VerificationStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<VerificationStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<VerificationStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<VerificationStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<VerificationStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<VerificationStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}