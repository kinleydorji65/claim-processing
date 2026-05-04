package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalCauseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/partial-withdrawal-causes")
@RequiredArgsConstructor
public class PartialWithdrawalCauseController {

    private final PartialWithdrawalCauseService service;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<PartialWithdrawalCauseResponseDto> create(
            @RequestBody PartialWithdrawalCauseRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<PartialWithdrawalCauseResponseDto> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalCauseRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<PartialWithdrawalCauseResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<List<PartialWithdrawalCauseResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // =========================
    // GET BY REASON ID (FK FILTER)
    // =========================
    @GetMapping("/by-reason/{reasonId}")
    public ResponseEntity<List<PartialWithdrawalCauseResponseDto>> getByReasonId(
            @PathVariable Long reasonId) {

        return ResponseEntity.ok(service.getByReasonId(reasonId));
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