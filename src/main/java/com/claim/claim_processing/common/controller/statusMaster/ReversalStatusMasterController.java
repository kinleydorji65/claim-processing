package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ReversalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.ReversalStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.ReversalStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/reversal-status")
@RequiredArgsConstructor
public class ReversalStatusMasterController {

    private final ReversalStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ReversalStatusResponseDto>> create(
            @RequestBody ReversalStatusRequestDto dto) {

        ApiResponseDTO<ReversalStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ReversalStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ReversalStatusRequestDto dto) {

        ApiResponseDTO<ReversalStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ReversalStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<ReversalStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<ReversalStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<ReversalStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ReversalStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<ReversalStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<ReversalStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<ReversalStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}