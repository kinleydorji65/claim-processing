package com.claim.claim_processing.common.controller.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationStageRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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
    public ResponseEntity<?> create(
            @RequestBody CalculationStageRequestDto request) {

        ApiResponseDTO<CalculationStageResponseDto> response =
                service.create(request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody CalculationStageRequestDto request) {

        ApiResponseDTO<CalculationStageResponseDto> response =
                service.update(id, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<CalculationStageResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<CalculationStageResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<CalculationStageResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}