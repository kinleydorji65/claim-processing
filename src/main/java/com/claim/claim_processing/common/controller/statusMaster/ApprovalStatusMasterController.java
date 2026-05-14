package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ApprovalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.ApprovalStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.ApprovalStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/approval-status")
@RequiredArgsConstructor
public class ApprovalStatusMasterController {

    private final ApprovalStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ApprovalStatusResponseDto>> create(
            @RequestBody ApprovalStatusRequestDto dto) {

        ApiResponseDTO<ApprovalStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ApprovalStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ApprovalStatusRequestDto dto) {

        ApiResponseDTO<ApprovalStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ApprovalStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<ApprovalStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<ApprovalStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<ApprovalStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ApprovalStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<ApprovalStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<ApprovalStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<ApprovalStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}