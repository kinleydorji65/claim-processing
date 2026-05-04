package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalRuleResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/partial-withdrawal-rule")
@RequiredArgsConstructor
public class PartialWithdrawalRuleController {

    private final PartialWithdrawalRuleService service;

    // -----------------------
    // CREATE
    // -----------------------
    @PostMapping
    public ResponseEntity<PartialWithdrawalRuleResponseDto> create(
            @RequestBody PartialWithdrawalRuleRequestDto dto
    ) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    // -----------------------
    // UPDATE (PATCH STYLE)
    // -----------------------
    @PatchMapping("/{id}")
    public ResponseEntity<PartialWithdrawalRuleResponseDto> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalRuleRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // -----------------------
    // GET BY ID
    // -----------------------
    @GetMapping("/{id}")
    public ResponseEntity<PartialWithdrawalRuleResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // -----------------------
    // GET ALL
    // -----------------------
    @GetMapping
    public ResponseEntity<List<PartialWithdrawalRuleResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // -----------------------
    // FILTER BY CATEGORY
    // -----------------------
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PartialWithdrawalRuleResponseDto>> getByCategory(
            @PathVariable String categoryId
    ) {
        return ResponseEntity.ok(service.getByCategory(categoryId));
    }

    // -----------------------
    // FILTER BY REASON
    // -----------------------
    @GetMapping("/reason/{reasonId}")
    public ResponseEntity<List<PartialWithdrawalRuleResponseDto>> getByReason(
            @PathVariable Long reasonId
    ) {
        return ResponseEntity.ok(service.getByReason(reasonId));
    }

    @GetMapping("/accumulation/{accumulationId}")
    public ResponseEntity<List<PartialWithdrawalRuleResponseDto>> getByAccumulation(
            @PathVariable Long accumulationId
    ) {
        return ResponseEntity.ok(service.getByAccumulation(accumulationId));
    }

    // -----------------------
    // DELETE
    // -----------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}