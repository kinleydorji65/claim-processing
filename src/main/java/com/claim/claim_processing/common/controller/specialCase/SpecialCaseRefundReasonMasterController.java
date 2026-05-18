package com.claim.claim_processing.common.controller.specialCase;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseRefundReasonResponseDto;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseRefundReasonMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/special-case-refund-reason")
@RequiredArgsConstructor
public class SpecialCaseRefundReasonMasterController {

    private final SpecialCaseRefundReasonMasterService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody SpecialCaseRefundReasonRequestDto requestDto) {

        ApiResponseDTO<SpecialCaseRefundReasonResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody SpecialCaseRefundReasonRequestDto requestDto) {

        ApiResponseDTO<SpecialCaseRefundReasonResponseDto> response =
                service.update(id, requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<SpecialCaseRefundReasonResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<SpecialCaseRefundReasonResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<SpecialCaseRefundReasonResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<SpecialCaseRefundReasonResponseDto>> response =
                service.getAllActive();

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