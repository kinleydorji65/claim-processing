package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialCauseResponseDto;
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
    public ResponseEntity<PartialCauseResponseDto> create(
            @Valid @RequestBody PartialCauseRequestDto requestDto
    ) {
        PartialCauseResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartialCauseResponseDto> getById(@PathVariable Long id) {
        PartialCauseResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PartialCauseResponseDto> getByCode(@PathVariable String code) {
        PartialCauseResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PartialCauseResponseDto>> getAll() {
        List<PartialCauseResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PartialCauseResponseDto>> getAllActive() {
        List<PartialCauseResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartialCauseResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PartialCauseUpdateDto updateDto
    ) {
        PartialCauseResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}