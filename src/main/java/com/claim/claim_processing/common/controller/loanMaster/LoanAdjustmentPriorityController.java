package com.claim.claim_processing.common.controller.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanAdjustmentPriorityRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanAdjustmentPriorityResponseDto;
import com.claim.claim_processing.common.service.loanMaster.LoanAdjustmentPriorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/loan-master/loan-adjustment-priority")
@RequiredArgsConstructor
public class LoanAdjustmentPriorityController {

    private final LoanAdjustmentPriorityService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LoanAdjustmentPriorityRequestDto dto) {
        ApiResponseDTO<LoanAdjustmentPriorityResponseDto> response = service.create(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody LoanAdjustmentPriorityRequestDto dto
    ) {
        ApiResponseDTO<LoanAdjustmentPriorityResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ApiResponseDTO<LoanAdjustmentPriorityResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {
        ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/loan-type/{loanTypeId}")
    public ResponseEntity<?> getByLoanTypeId(@PathVariable Long loanTypeId) {
        ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> response =
                service.getByLoanTypeId(loanTypeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}