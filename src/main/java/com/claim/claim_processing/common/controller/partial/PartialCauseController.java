package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialCauseUpdateDto;
import com.claim.claim_processing.common.service.partial.PartialCauseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/partial-causes")
@RequiredArgsConstructor
public class PartialCauseController {

    private final PartialCauseService service;

    @PostMapping
    public ResponseEntity<PartialWithdrawalCauseResponseDto> create(
            @Valid @RequestBody PartialCauseRequestDto requestDto
    ) {
        PartialWithdrawalCauseResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartialWithdrawalCauseResponseDto> getById(@PathVariable Long id) {
        PartialWithdrawalCauseResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PartialWithdrawalCauseResponseDto> getByCode(@PathVariable String code) {
        PartialWithdrawalCauseResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PartialWithdrawalCauseResponseDto>> getAll() {
        List<PartialWithdrawalCauseResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PartialWithdrawalCauseResponseDto>> getAllActive() {
        List<PartialWithdrawalCauseResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartialWithdrawalCauseResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PartialCauseUpdateDto updateDto
    ) {
        PartialWithdrawalCauseResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}