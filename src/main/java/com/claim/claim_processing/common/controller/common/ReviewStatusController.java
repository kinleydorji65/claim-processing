package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.ReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.service.common.ReviewStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/review-status")
@RequiredArgsConstructor
public class ReviewStatusController {

    private final ReviewStatusService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReviewStatusRequestDto dto) {

        ApiResponseDTO<ReviewStatusResponseDto> response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -----------------------------
    // UPDATE (PATCH)
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ReviewStatusRequestDto dto) {

        ApiResponseDTO<ReviewStatusResponseDto> response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<ReviewStatusResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<ReviewStatusResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<ReviewStatusResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<ReviewStatusResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // SOFT DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}