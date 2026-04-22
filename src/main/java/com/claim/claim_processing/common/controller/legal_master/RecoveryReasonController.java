package com.claim.claim_processing.common.controller.legal_master;

import com.claim.claim_processing.common.DTO.request.legal_master.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.legal_master.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legal_master.RecoveryReasonUpdateDto;
import com.claim.claim_processing.common.service.legal_master.RecoveryReasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/recovery-reasons")
@RequiredArgsConstructor
public class RecoveryReasonController {

    private final RecoveryReasonService service;

    @PostMapping
    public ResponseEntity<RecoveryReasonResponseDto> create(
            @Valid @RequestBody RecoveryReasonRequestDto requestDto
    ) {
        RecoveryReasonResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecoveryReasonResponseDto> getById(@PathVariable Long id) {
        RecoveryReasonResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<RecoveryReasonResponseDto> getByCode(@PathVariable String code) {
        RecoveryReasonResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RecoveryReasonResponseDto>> getAll() {
        List<RecoveryReasonResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<RecoveryReasonResponseDto>> getAllActive() {
        List<RecoveryReasonResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecoveryReasonResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody RecoveryReasonUpdateDto updateDto
    ) {
        RecoveryReasonResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}