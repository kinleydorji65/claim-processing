package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionReferenceTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DeductionReferenceTypeResponseDto;
import com.claim.claim_processing.common.service.common.DeductionReferenceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common/deduction-reference-types")
@RequiredArgsConstructor
public class DeductionReferenceTypeController {

    private final DeductionReferenceTypeService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody DeductionReferenceTypeRequestDto dto) {
        ApiResponseDTO<DeductionReferenceTypeResponseDto> response = service.create(dto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {
        ApiResponseDTO<List<DeductionReferenceTypeResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {
        ApiResponseDTO<List<DeductionReferenceTypeResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ApiResponseDTO<DeductionReferenceTypeResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        ApiResponseDTO<DeductionReferenceTypeResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DeductionReferenceTypeRequestDto dto
    ) {
        ApiResponseDTO<DeductionReferenceTypeResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}