package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalRuleResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/partial-withdrawal-rule")
@RequiredArgsConstructor
public class PartialWithdrawalRuleController {

    private final PartialWithdrawalRuleService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PartialWithdrawalRuleRequestDto dto) {

        ApiResponseDTO<PartialWithdrawalRuleResponseDto> response = service.create(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalRuleRequestDto dto) {

        ApiResponseDTO<PartialWithdrawalRuleResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<PartialWithdrawalRuleResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getByCategory(@PathVariable String categoryId) {

        ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> response = service.getByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reason/{reasonId}")
    public ResponseEntity<?> getByReason(@PathVariable Long reasonId) {

        ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> response = service.getByReason(reasonId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accumulation/{accumulationId}")
    public ResponseEntity<?> getByAccumulation(@PathVariable Long accumulationId) {

        ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> response = service.getByAccumulation(accumulationId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}