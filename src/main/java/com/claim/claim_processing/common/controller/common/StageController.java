package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.StageRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.service.common.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/stage")
@RequiredArgsConstructor
public class StageController {

    private final StageService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody StageRequestDto dto
    ) {

        ApiResponseDTO<StageResponseDto> response =
                service.create(dto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody StageRequestDto dto
    ) {

        ApiResponseDTO<StageResponseDto> response =
                service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<StageResponseDto> response =
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

        ApiResponseDTO<StageResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<StageResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<StageResponseDto>> response =
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