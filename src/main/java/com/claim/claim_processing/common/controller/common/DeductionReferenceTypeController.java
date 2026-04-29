package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionReferenceTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionReferenceTypeResponseDto;
import com.claim.claim_processing.common.service.common.DeductionReferenceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common/deduction-reference-types")
@RequiredArgsConstructor
public class DeductionReferenceTypeController {

    private final DeductionReferenceTypeService service;

    @PostMapping
    public ResponseEntity<DeductionReferenceTypeResponseDto> create(@RequestBody DeductionReferenceTypeRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<DeductionReferenceTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<DeductionReferenceTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeductionReferenceTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DeductionReferenceTypeResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeductionReferenceTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody DeductionReferenceTypeRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}