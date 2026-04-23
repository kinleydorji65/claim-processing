package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimSourceRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimSourceResponseDto;
import com.claim.claim_processing.common.DTO.update.common.ClaimSourceUpdateDto;
import com.claim.claim_processing.common.service.common.ClaimSourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Claim Source", description = "Claim source master APIs")
@RestController
@RequestMapping("/api/claim/masters/claim-sources")
@RequiredArgsConstructor
public class ClaimSourceController {

    private final ClaimSourceService service;

    @GetMapping
    public ResponseEntity<List<ClaimSourceResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimSourceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ClaimSourceResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @PostMapping
    public ResponseEntity<ClaimSourceResponseDto> create(
            @RequestBody ClaimSourceRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimSourceResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimSourceUpdateDto updateDto) {
        return ResponseEntity.ok(service.update(id, updateDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}