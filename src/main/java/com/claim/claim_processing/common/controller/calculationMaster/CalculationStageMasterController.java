package com.claim.claim_processing.common.controller.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationStageRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationStageResponseDto;
import com.claim.claim_processing.common.service.calculationMaster.CalculationStageMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/calculation-stage")
@RequiredArgsConstructor
public class CalculationStageMasterController {

    private final CalculationStageMasterService service;

    @PostMapping
    public ResponseEntity<CalculationStageResponseDto> create(
            @RequestBody CalculationStageRequestDto request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CalculationStageResponseDto> update(
            @PathVariable Long id,
            @RequestBody CalculationStageRequestDto request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalculationStageResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CalculationStageResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<CalculationStageResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}