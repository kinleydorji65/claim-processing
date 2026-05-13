package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.SubmissionChannelRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.SubmissionChannelResponseDto;
import com.claim.claim_processing.common.DTO.update.common.SubmissionChannelUpdateDto;
import com.claim.claim_processing.common.service.common.SubmissionChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/submission-channels")
@RequiredArgsConstructor
public class SubmissionChannelController {

    private final SubmissionChannelService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody SubmissionChannelRequestDto requestDto
    ) {

        ApiResponseDTO<SubmissionChannelResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody SubmissionChannelUpdateDto updateDto
    ) {

        ApiResponseDTO<SubmissionChannelResponseDto> response =
                service.update(id, updateDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<SubmissionChannelResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code
    ) {

        ApiResponseDTO<SubmissionChannelResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<SubmissionChannelResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<SubmissionChannelResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // SOFT DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}