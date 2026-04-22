package com.claim.claim_processing.common.controller.refund_master;

import com.claim.claim_processing.common.DTO.request.refund_master.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.refund_master.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refund_master.RefundScopeUpdateDto;
import com.claim.claim_processing.common.service.refund_master.RefundScopeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/refund-scopes")
@RequiredArgsConstructor
public class RefundScopeController {

    private final RefundScopeService service;

    @PostMapping
    public ResponseEntity<RefundScopeResponseDto> create(
            @Valid @RequestBody RefundScopeRequestDto requestDto
    ) {
        RefundScopeResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefundScopeResponseDto> getById(@PathVariable Long id) {
        RefundScopeResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<RefundScopeResponseDto> getByCode(@PathVariable String code) {
        RefundScopeResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RefundScopeResponseDto>> getAll() {
        List<RefundScopeResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<RefundScopeResponseDto>> getAllActive() {
        List<RefundScopeResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefundScopeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody RefundScopeUpdateDto updateDto
    ) {
        RefundScopeResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}