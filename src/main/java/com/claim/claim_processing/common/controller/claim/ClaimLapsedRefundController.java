package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimLapsedRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<ClaimLapsedRefundResponseDto> create(
            @RequestBody ClaimLapsedRefundRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ClaimLapsedRefundResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<List<ClaimLapsedRefundResponseDto>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    // -------------------------------
    // UPDATE
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ClaimLapsedRefundResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimLapsedRefundRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
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
    public ResponseEntity<List<ClaimLapsedRefundResponseDto>> getActiveRules() {

        return ResponseEntity.ok(service.getActiveRules());
    }

    @GetMapping("/valid")
    public ResponseEntity<List<ClaimLapsedRefundResponseDto>> getValidRulesByDate(
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(service.getValidRulesByDate(date));
    }

    @GetMapping("/rule-code/{ruleCode}")
    public ResponseEntity<ClaimLapsedRefundResponseDto> getByRuleCode(
            @PathVariable String ruleCode) {

        return ResponseEntity.ok(service.getByRuleCode(ruleCode));
    }

    // -------------------------------
    // FK FILTER APIs (ADMIN/UI)
    // -------------------------------
    @GetMapping("/claim-circumstance/{id}")
    public ResponseEntity<List<ClaimLapsedRefundResponseDto>> getByClaimCircumstance(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getByClaimCircumstance(id));
    }

    @GetMapping("/cessation-type/{id}")
    public ResponseEntity<List<ClaimLapsedRefundResponseDto>> getByCessationType(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getByCessationType(id));
    }

    @GetMapping("/scheme-type/{id}")
    public ResponseEntity<List<ClaimLapsedRefundResponseDto>> getBySchemeType(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getBySchemeType(id));
    }
}