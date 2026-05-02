package com.claim.claim_processing.common.controller.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanStatusResponseDto;
import com.claim.claim_processing.common.service.loanMaster.LoanStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/loan-master/loan-status")
@RequiredArgsConstructor
public class LoanStatusController {

    private final LoanStatusService service;

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<LoanStatusResponseDto> create(
            @RequestBody LoanStatusRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 UPDATE (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<LoanStatusResponseDto> update(
            @PathVariable Long id,
            @RequestBody LoanStatusRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanStatusResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 GET BY CODE
    @GetMapping("/code/{code}")
    public ResponseEntity<LoanStatusResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<LoanStatusResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 GET ALL ACTIVE
    @GetMapping("/active")
    public ResponseEntity<List<LoanStatusResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Loan Status deactivated successfully");
    }
}