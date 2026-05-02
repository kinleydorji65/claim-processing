package com.claim.claim_processing.common.controller.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanTypeResponseDto;
import com.claim.claim_processing.common.service.loanMaster.LoanTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/loan-master/loan-type")
@RequiredArgsConstructor
public class LoanTypeController {

    private final LoanTypeService service;

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<LoanTypeResponseDto> create(
            @RequestBody LoanTypeRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<LoanTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody LoanTypeRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanTypeResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 GET BY CODE
    @GetMapping("/code/{code}")
    public ResponseEntity<LoanTypeResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<LoanTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 GET ALL ACTIVE
    @GetMapping("/active")
    public ResponseEntity<List<LoanTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Loan Type deactivated successfully");
    }
}