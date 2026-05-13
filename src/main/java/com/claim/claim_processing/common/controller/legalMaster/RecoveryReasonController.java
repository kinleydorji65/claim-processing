package com.claim.claim_processing.common.controller.legalMaster;

import com.claim.claim_processing.common.DTO.request.legalMaster.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.legalMaster.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legalMaster.RecoveryReasonUpdateDto;
import com.claim.claim_processing.common.service.legalMaster.RecoveryReasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/recovery-reasons")
@RequiredArgsConstructor
public class RecoveryReasonController {

    private final RecoveryReasonService service;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody RecoveryReasonRequestDto requestDto
    ) {

        ApiResponseDTO<RecoveryReasonResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    // =========================
    // UPDATE (PATCH)
    // =========================
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody RecoveryReasonUpdateDto updateDto
    ) {

        ApiResponseDTO<RecoveryReasonResponseDto> response =
                service.update(id, updateDto);

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<RecoveryReasonResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET BY CODE
    // =========================
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code
    ) {

        ApiResponseDTO<RecoveryReasonResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<RecoveryReasonResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET ALL ACTIVE
    // =========================
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<RecoveryReasonResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}