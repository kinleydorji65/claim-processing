package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimTypeDeductionMapRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimTypeDeductionMapResponseDto;
import com.claim.claim_processing.common.service.common.ClaimTypeDeductionMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/claim-type-deduction-map")
@RequiredArgsConstructor
public class ClaimTypeDeductionMapController {

    private final ClaimTypeDeductionMapService service;

    // ---------------------------------------------------
    // CREATE
    // ---------------------------------------------------
    @PostMapping
    public ResponseEntity<ClaimTypeDeductionMapResponseDto> create(
            @RequestBody ClaimTypeDeductionMapRequestDto dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    // ---------------------------------------------------
    // UPDATE (FULL REPLACE)
    // ---------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ClaimTypeDeductionMapResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimTypeDeductionMapRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // ---------------------------------------------------
    // PATCH (PARTIAL UPDATE)
    // ---------------------------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<ClaimTypeDeductionMapResponseDto> patch(
            @PathVariable Long id,
            @RequestBody ClaimTypeDeductionMapRequestDto dto
    ) {
        return ResponseEntity.ok(service.patch(id, dto));
    }

    // ---------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ClaimTypeDeductionMapResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ---------------------------------------------------
    // GET ALL
    // ---------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ClaimTypeDeductionMapResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ---------------------------------------------------
    // FK API: BY CLAIM TYPE
    // ---------------------------------------------------
    @GetMapping("/claim-type/{claimTypeId}")
    public ResponseEntity<List<ClaimTypeDeductionMapResponseDto>> getByClaimTypeId(
            @PathVariable Long claimTypeId
    ) {
        return ResponseEntity.ok(service.getByClaimTypeId(claimTypeId));
    }

    // ---------------------------------------------------
    // FK API: BY DEDUCTION TYPE
    // ---------------------------------------------------
    @GetMapping("/deduction-type/{deductionTypeId}")
    public ResponseEntity<List<ClaimTypeDeductionMapResponseDto>> getByDeductionTypeId(
            @PathVariable Long deductionTypeId
    ) {
        return ResponseEntity.ok(service.getByDeductionTypeId(deductionTypeId));
    }

    // ---------------------------------------------------
    // DELETE
    // ---------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}