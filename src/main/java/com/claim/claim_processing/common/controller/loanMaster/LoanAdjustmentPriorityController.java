package com.claim.claim_processing.common.controller.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanAdjustmentPriorityRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanAdjustmentPriorityResponseDto;
import com.claim.claim_processing.common.service.loanMaster.LoanAdjustmentPriorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/loan-master/loan-adjustment-priority")
@RequiredArgsConstructor
public class LoanAdjustmentPriorityController {

    private final LoanAdjustmentPriorityService service;

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<LoanAdjustmentPriorityResponseDto> create(
            @RequestBody LoanAdjustmentPriorityRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<LoanAdjustmentPriorityResponseDto> update(
            @PathVariable Long id,
            @RequestBody LoanAdjustmentPriorityRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanAdjustmentPriorityResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<LoanAdjustmentPriorityResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 GET ALL ACTIVE
    @GetMapping("/active")
    public ResponseEntity<List<LoanAdjustmentPriorityResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 GET BY LOAN TYPE FK
    @GetMapping("/loan-type/{loanTypeId}")
    public ResponseEntity<List<LoanAdjustmentPriorityResponseDto>> getByLoanTypeId(
            @PathVariable Long loanTypeId
    ) {
        return ResponseEntity.ok(service.getByLoanTypeId(loanTypeId));
    }

    // 🔹 SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Loan Adjustment Priority deactivated successfully");
    }
}