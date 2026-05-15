package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.service.partial.PartialWithdrawalCauseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/partial-withdrawal-causes")
@RequiredArgsConstructor
public class PartialWithdrawalCauseController {

    private final PartialWithdrawalCauseService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PartialWithdrawalCauseRequestDto requestDto) {

        ApiResponseDTO<PartialWithdrawalCauseResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<PartialWithdrawalCauseResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<PartialWithdrawalCauseResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reason/{reasonId}")
    public ResponseEntity<?> getByReasonId(@PathVariable Long reasonId) {

        ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> response =
                service.getByReason_Id(reasonId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PartialWithdrawalCauseRequestDto updateDto) {

        ApiResponseDTO<PartialWithdrawalCauseResponseDto> response =
                service.update(id, updateDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}