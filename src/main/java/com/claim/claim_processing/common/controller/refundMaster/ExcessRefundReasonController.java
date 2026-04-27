package com.claim.claim_processing.common.controller.refundMaster;

import com.claim.claim_processing.common.DTO.request.refundMaster.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.refundMaster.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.ExcessRefundReasonUpdateDto;
import com.claim.claim_processing.common.service.refundMaster.ExcessRefundReasonService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/excess-refund-reasons")
@RequiredArgsConstructor
public class ExcessRefundReasonController {

    private final ExcessRefundReasonService service;

    @PostMapping
    public ResponseEntity<ExcessRefundReasonResponseDto> create(
            @Valid @RequestBody ExcessRefundReasonRequestDto requestDto
    ) {
        ExcessRefundReasonResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcessRefundReasonResponseDto> getById(@PathVariable Long id) {
        ExcessRefundReasonResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ExcessRefundReasonResponseDto> getByCode(@PathVariable String code) {
        ExcessRefundReasonResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ExcessRefundReasonResponseDto>> getAll() {
        List<ExcessRefundReasonResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ExcessRefundReasonResponseDto>> getAllActive() {
        List<ExcessRefundReasonResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExcessRefundReasonResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ExcessRefundReasonUpdateDto updateDto
    ) {
        ExcessRefundReasonResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}