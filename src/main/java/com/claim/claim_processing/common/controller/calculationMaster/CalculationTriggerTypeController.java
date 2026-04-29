package com.claim.claim_processing.common.controller.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationTriggerTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationTriggerTypeResponseDto;
import com.claim.claim_processing.common.service.calculationMaster.CalculationTriggerTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/calculation-trigger-type")
@RequiredArgsConstructor
public class CalculationTriggerTypeController {

    private final CalculationTriggerTypeService service;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    public ResponseEntity<CalculationTriggerTypeResponseDto> create(
            @RequestBody CalculationTriggerTypeRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // -------------------------------
    // PATCH ONLY (NO PUT)
    // -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<CalculationTriggerTypeResponseDto> patch(
            @PathVariable Long id,
            @RequestBody CalculationTriggerTypeRequestDto dto) {

        dto.setId(id);
        return ResponseEntity.ok(service.patch(dto));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<CalculationTriggerTypeResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<List<CalculationTriggerTypeResponseDto>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @GetMapping("/active")
    public ResponseEntity<List<CalculationTriggerTypeResponseDto>> getAllActive() {

        return ResponseEntity.ok(service.getAllActive());
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