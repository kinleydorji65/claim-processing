package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.TerminationReasonCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.TerminationReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.TerminationReasonUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.TerminationReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/termination-reasons")
@RequiredArgsConstructor
public class TerminationReasonController {

    private final TerminationReasonService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody TerminationReasonCreateRequestDto requestDto
    ) {

        ApiResponseDTO<TerminationReasonResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody TerminationReasonUpdateRequestDto requestDto
    ) {
        ApiResponseDTO<TerminationReasonResponseDto> response =
                service.update(id, requestDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {
        ApiResponseDTO<TerminationReasonResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAllActive() {
        ApiResponseDTO<List<TerminationReasonResponseDto>> response =
                service.getAllActive();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DEACTIVATE
    // -----------------------------
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(
            @PathVariable Long id
    ) {
        ApiResponseDTO<String> response =
                service.deactivate(id);
        return ResponseEntity.ok(response);
    }
}