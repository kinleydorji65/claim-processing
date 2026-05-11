package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.CessationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/cessation-types")
@RequiredArgsConstructor
public class CessationTypeController {

    private final CessationTypeService service;

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<CessationTypeResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET ACTIVE
    // =========================
    @GetMapping("/active")
    public ResponseEntity<?> getActive() {

        ApiResponseDTO<List<CessationTypeResponseDto>> response =
                service.getActive();

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<CessationTypeResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET BY CLAIM CIRCUMSTANCE
    // =========================
    @GetMapping("/circumstance/{circumstanceId}")
    public ResponseEntity<?> getByClaimCircumstance(
            @PathVariable Long circumstanceId) {

        ApiResponseDTO<List<CessationTypeResponseDto>> response =
                service.getByClaimCircumstance(circumstanceId);

        return ResponseEntity.ok(response);
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CessationTypeCreateRequestDto requestDto) {

        ApiResponseDTO<CessationTypeResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody CessationTypeUpdateRequestDto requestDto) {

        ApiResponseDTO<CessationTypeResponseDto> response =
                service.update(id, requestDto);

        return ResponseEntity.ok(response);
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}