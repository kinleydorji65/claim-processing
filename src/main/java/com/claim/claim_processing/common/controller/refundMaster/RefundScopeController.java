package com.claim.claim_processing.common.controller.refundMaster;

import com.claim.claim_processing.common.DTO.request.refundMaster.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.refundMaster.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.RefundScopeUpdateDto;
import com.claim.claim_processing.common.service.refundMaster.RefundScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/refund-scopes")
@RequiredArgsConstructor
public class RefundScopeController {

    private final RefundScopeService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody RefundScopeRequestDto requestDto) {

        ApiResponseDTO<RefundScopeResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<RefundScopeResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<RefundScopeResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<RefundScopeResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<RefundScopeResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody RefundScopeUpdateDto updateDto) {

        ApiResponseDTO<RefundScopeResponseDto> response =
                service.update(id, updateDto);

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