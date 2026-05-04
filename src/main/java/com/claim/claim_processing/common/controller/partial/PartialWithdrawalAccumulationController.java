package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalAccumulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/partial-withdrawal-accumulations")
@RequiredArgsConstructor
public class PartialWithdrawalAccumulationController {

    private final PartialWithdrawalAccumulationService service;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<PartialWithdrawalAccumulationResponseDto> create(
            @RequestBody PartialWithdrawalAccumulationRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<PartialWithdrawalAccumulationResponseDto> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalAccumulationRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<PartialWithdrawalAccumulationResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // =========================
    // GET ALL (INCLUDES ACTIVE + INACTIVE)
    // =========================
    @GetMapping
    public ResponseEntity<List<PartialWithdrawalAccumulationResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // =========================
    // GET ALL ACTIVE ONLY
    // =========================
    @GetMapping("/active")
    public ResponseEntity<List<PartialWithdrawalAccumulationResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // =========================
    // DELETE (SOFT DELETE)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}