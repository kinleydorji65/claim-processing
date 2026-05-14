package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.ClaimCircumstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/claim-circumstances")
@RequiredArgsConstructor
public class ClaimCircumstanceController {

    private final ClaimCircumstanceService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ClaimCircumstanceCreateRequestDto requestDto) {

        ApiResponseDTO<ClaimCircumstanceResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<ClaimCircumstanceResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<ClaimCircumstanceResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<ClaimCircumstanceResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ClaimCircumstanceUpdateRequestDto requestDto) {

        ApiResponseDTO<ClaimCircumstanceResponseDto> response = service.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<ClaimCircumstanceResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }
}