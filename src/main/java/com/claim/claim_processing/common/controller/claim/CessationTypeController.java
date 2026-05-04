package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.CessationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/cessation-types")
@RequiredArgsConstructor
public class CessationTypeController {

    private final CessationTypeService service;

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<List<CessationTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // =========================
    // GET ACTIVE
    // =========================
    @GetMapping("/active")
    public ResponseEntity<List<CessationTypeResponseDto>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<CessationTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =========================
    // GET BY CLAIM CIRCUMSTANCE
    // =========================
    @GetMapping("/circumstance/{circumstanceId}")
    public ResponseEntity<List<CessationTypeResponseDto>> getByClaimCircumstance(
            @PathVariable Long circumstanceId
    ) {
        return ResponseEntity.ok(service.getByClaimCircumstance(circumstanceId));
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<CessationTypeResponseDto> create(
            @RequestBody CessationTypeCreateRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(requestDto));
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<CessationTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody CessationTypeUpdateRequestDto requestDto
    ) {
        return ResponseEntity.ok(service.update(id, requestDto));
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}