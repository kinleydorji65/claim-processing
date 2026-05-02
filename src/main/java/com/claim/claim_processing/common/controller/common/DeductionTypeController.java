package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionTypeResponseDto;
import com.claim.claim_processing.common.service.common.DeductionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/deduction-types")
@RequiredArgsConstructor
public class DeductionTypeController {

    private final DeductionTypeService service;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    public ResponseEntity<DeductionTypeResponseDto> create(
            @RequestBody DeductionTypeRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DeductionTypeResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // -------------------------------
    // GET BY CODE (BUSINESS KEY)
    // -------------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<DeductionTypeResponseDto> getByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(service.getByCode(code));
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @GetMapping("/active")
    public ResponseEntity<List<DeductionTypeResponseDto>> getAllActive() {

        return ResponseEntity.ok(service.getAllActive());
    }

    // -------------------------------
    // UPDATE (PATCH STYLE)
    // -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<DeductionTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody DeductionTypeRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // -------------------------------
    // DELETE (SOFT DELETE)
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}