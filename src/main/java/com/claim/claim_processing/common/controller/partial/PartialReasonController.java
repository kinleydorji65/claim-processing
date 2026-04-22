package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialReasonUpdateDto;
import com.claim.claim_processing.common.service.partial.PartialReasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/partial-reasons")
@RequiredArgsConstructor
public class PartialReasonController {

    private final PartialReasonService service;

    @PostMapping
    public ResponseEntity<PartialReasonResponseDto> create(
            @Valid @RequestBody PartialReasonRequestDto requestDto
    ) {
        PartialReasonResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartialReasonResponseDto> getById(@PathVariable Long id) {
        PartialReasonResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PartialReasonResponseDto> getByCode(@PathVariable String code) {
        PartialReasonResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PartialReasonResponseDto>> getAll() {
        List<PartialReasonResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PartialReasonResponseDto>> getAllActive() {
        List<PartialReasonResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartialReasonResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PartialReasonUpdateDto updateDto
    ) {
        PartialReasonResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}