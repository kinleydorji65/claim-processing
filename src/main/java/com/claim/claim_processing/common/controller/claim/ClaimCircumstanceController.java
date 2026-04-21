package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.ClaimCircumstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/claim-circumstances")
@RequiredArgsConstructor
public class ClaimCircumstanceController {

    private final ClaimCircumstanceService service;

    @GetMapping
    public ResponseEntity<List<ClaimCircumstanceResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimCircumstanceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ClaimCircumstanceResponseDto> create(
            @RequestBody ClaimCircumstanceCreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimCircumstanceResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimCircumstanceUpdateRequestDto requestDto) {
        return ResponseEntity.ok(service.update(id, requestDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}