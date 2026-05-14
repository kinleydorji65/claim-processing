package com.claim.claim_processing.common.controller.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RuleEvaluationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.apiResponse.ApiResponse;
import com.claim.claim_processing.common.DTO.response.statusMaster.RuleEvaluationStatusResponseDto;
import com.claim.claim_processing.common.service.statusMaster.RuleEvaluationStatusMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/rule-evaluation-status")
@RequiredArgsConstructor
public class RuleEvaluationStatusMasterController {

    private final RuleEvaluationStatusMasterService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RuleEvaluationStatusResponseDto>> create(
            @RequestBody RuleEvaluationStatusRequestDto dto) {

        ApiResponseDTO<RuleEvaluationStatusResponseDto> response = service.create(dto);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE (PATCH) =================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RuleEvaluationStatusResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RuleEvaluationStatusRequestDto dto) {

        ApiResponseDTO<RuleEvaluationStatusResponseDto> response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RuleEvaluationStatusResponseDto>> getById(
            @PathVariable Long id) {

        ApiResponseDTO<RuleEvaluationStatusResponseDto> response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<RuleEvaluationStatusResponseDto>> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<RuleEvaluationStatusResponseDto> response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RuleEvaluationStatusResponseDto>>> getAll() {

        ApiResponseDTO<List<RuleEvaluationStatusResponseDto>> response = service.getAll();

        return ResponseEntity.ok(response);
    }

    // ================= GET ALL ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<RuleEvaluationStatusResponseDto>>> getAllActive() {

        ApiResponseDTO<List<RuleEvaluationStatusResponseDto>> response = service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);

        return ResponseEntity.ok(response);
    }
}