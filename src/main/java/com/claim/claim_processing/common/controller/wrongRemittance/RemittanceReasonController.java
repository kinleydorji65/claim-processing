package com.claim.claim_processing.common.controller.wrongRemittance;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrongRemittance.RemittanceReasonUpdateDto;
import com.claim.claim_processing.common.service.wrongRemittance.RemittanceReasonService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Remittance Reason", description = "Wrong remittance reason master APIs")
@RestController
@RequestMapping("/api/claim/masters/remittance-reasons")
@RequiredArgsConstructor
public class RemittanceReasonController {

    private final RemittanceReasonService service;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RemittanceReasonResponseDto>>> getAllActive() {
        ApiResponseDTO<List<RemittanceReasonResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RemittanceReasonResponseDto>> getById(@PathVariable Long id) {
        ApiResponseDTO<RemittanceReasonResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<RemittanceReasonResponseDto>> getByCode(@PathVariable String code) {
        ApiResponseDTO<RemittanceReasonResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<RemittanceReasonResponseDto>> create(
            @RequestBody RemittanceReasonRequestDto requestDto) {
        ApiResponseDTO<RemittanceReasonResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RemittanceReasonResponseDto>> update(
            @PathVariable Long id,
            @RequestBody RemittanceReasonUpdateDto updateDto) {
        ApiResponseDTO<RemittanceReasonResponseDto> response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponseDTO<String>> deactivate(@PathVariable Long id) {
        ApiResponseDTO<String> response = service.deactivate(id);
        return ResponseEntity.ok(response);
    }
}