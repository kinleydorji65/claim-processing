package com.claim.claim_processing.common.controller.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanStatusResponseDto;
import com.claim.claim_processing.common.service.loanMaster.LoanStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/loan-master/loan-status")
@RequiredArgsConstructor
public class LoanStatusController {

    private final LoanStatusService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody LoanStatusRequestDto dto) {
        ApiResponseDTO<LoanStatusResponseDto> response = service.create(dto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody LoanStatusRequestDto dto
    ) {
        ApiResponseDTO<LoanStatusResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ApiResponseDTO<LoanStatusResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        ApiResponseDTO<LoanStatusResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {
        ApiResponseDTO<List<LoanStatusResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {
        ApiResponseDTO<List<LoanStatusResponseDto>> response = service.getAllActive();
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