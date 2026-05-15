package com.claim.claim_processing.common.controller.refundMaster;

import com.claim.claim_processing.common.DTO.request.refundMaster.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.refundMaster.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.ExcessRefundReasonUpdateDto;
import com.claim.claim_processing.common.service.refundMaster.ExcessRefundReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/excess-refund-reasons")
@RequiredArgsConstructor
public class ExcessRefundReasonController {

    private final ExcessRefundReasonService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ExcessRefundReasonRequestDto requestDto) {

        ApiResponseDTO<ExcessRefundReasonResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<ExcessRefundReasonResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<ExcessRefundReasonResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<ExcessRefundReasonResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<ExcessRefundReasonResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ExcessRefundReasonUpdateDto updateDto) {

        ApiResponseDTO<ExcessRefundReasonResponseDto> response =
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