package com.claim.claim_processing.common.controller.wrong_remittance;

import com.claim.claim_processing.common.DTO.request.wrong_remittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.wrong_remittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrong_remittance.RemittanceReasonUpdateDto;
import com.claim.claim_processing.common.service.wrongRemittance.RemittanceReasonService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Remittance Reason", description = "Wrong remittance reason master APIs")
@RestController
@RequestMapping("/api/claim/masters/remittance-reasons")
@RequiredArgsConstructor
public class RemittanceReasonController {

    private final RemittanceReasonService service;

    @GetMapping
    public ResponseEntity<List<RemittanceReasonResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RemittanceReasonResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<RemittanceReasonResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @PostMapping
    public ResponseEntity<RemittanceReasonResponseDto> create(
            @RequestBody RemittanceReasonRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(requestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RemittanceReasonResponseDto> update(
            @PathVariable Long id,
            @RequestBody RemittanceReasonUpdateDto updateDto) {
        return ResponseEntity.ok(service.update(id, updateDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}