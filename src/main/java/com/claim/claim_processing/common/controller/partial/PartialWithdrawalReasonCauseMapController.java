package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonCauseMapRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonCauseMapResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalReasonCauseMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partial-withdrawal/reason-cause-map")
@RequiredArgsConstructor
public class PartialWithdrawalReasonCauseMapController {

    private final PartialWithdrawalReasonCauseMapService service;

    // -----------------------
    // CREATE
    // -----------------------
    @PostMapping
    public ResponseEntity<PartialWithdrawalReasonCauseMapResponseDto> create(
            @RequestBody PartialWithdrawalReasonCauseMapRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // -----------------------
    // UPDATE (PATCH STYLE)
    // -----------------------
    @PatchMapping("/{id}")
    public ResponseEntity<PartialWithdrawalReasonCauseMapResponseDto> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalReasonCauseMapRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // -----------------------
    // GET BY ID
    // -----------------------
    @GetMapping("/{id}")
    public ResponseEntity<PartialWithdrawalReasonCauseMapResponseDto> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // -----------------------
    // GET ALL
    // -----------------------
    @GetMapping
    public ResponseEntity<List<PartialWithdrawalReasonCauseMapResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // -----------------------
    // FK FILTERS
    // -----------------------
    @GetMapping("/reason/{reasonId}")
    public ResponseEntity<List<PartialWithdrawalReasonCauseMapResponseDto>> getByReasonId(
            @PathVariable Long reasonId) {
        return ResponseEntity.ok(service.getByReasonId(reasonId));
    }

    @GetMapping("/cause/{causeId}")
    public ResponseEntity<List<PartialWithdrawalReasonCauseMapResponseDto>> getByCauseId(
            @PathVariable Long causeId) {
        return ResponseEntity.ok(service.getByCauseId(causeId));
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