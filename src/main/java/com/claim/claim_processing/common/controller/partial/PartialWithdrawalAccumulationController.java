package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalAccumulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/partial-withdrawal-accumulations")
@RequiredArgsConstructor
public class PartialWithdrawalAccumulationController {

    private final PartialWithdrawalAccumulationService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PartialWithdrawalAccumulationRequestDto dto) {

        ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> response = service.create(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalAccumulationRequestDto dto) {

        ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<PartialWithdrawalAccumulationResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<PartialWithdrawalAccumulationResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}