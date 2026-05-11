package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/claim/lapsed-refund")
@RequiredArgsConstructor
public class ClaimLapsedRefundController {

    private final ClaimLapsedRefundService service;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ClaimLapsedRefundRequestDto dto) {

        ApiResponseDTO<ClaimLapsedRefundResponseDto> response = service.create(dto);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<ClaimLapsedRefundResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {
        ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ClaimLapsedRefundRequestDto dto) {

        ApiResponseDTO<ClaimLapsedRefundResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------
    // RULE ENGINE APIs
    // -------------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getActiveRules() {
        ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> response = service.getActiveRules();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rule-code/{ruleCode}")
    public ResponseEntity<?> getByRuleCode(
            @PathVariable String ruleCode) {

        ApiResponseDTO<ClaimLapsedRefundResponseDto> response = service.getByRuleCode(ruleCode);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // FK FILTER APIs (ADMIN/UI)
    // -------------------------------
    @GetMapping("/claim-circumstance/{id}")
    public ResponseEntity<?> getByClaimCircumstance(
            @PathVariable Long id) {

        ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> response = service.getByClaimCircumstance(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scheme-type/{id}")
    public ResponseEntity<?> getBySchemeType(
            @PathVariable Long id) {

        ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> response = service.getBySchemeType(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rule-type/{id}")
    public ResponseEntity<?> getByRuleType(
            @PathVariable Long id) {
        ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> response = service.getByRuleType(id);
        return ResponseEntity.ok(response);
    }
}