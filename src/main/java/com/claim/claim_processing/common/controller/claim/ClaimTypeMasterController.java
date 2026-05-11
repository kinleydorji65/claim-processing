package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;
import com.claim.claim_processing.common.service.claim.ClaimTypeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/claims/type-master")
@RequiredArgsConstructor
public class ClaimTypeMasterController {

    private final ClaimTypeMasterService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ClaimTypeMasterRequestDto requestDto) {
            ApiResponseDTO<ClaimTypeMasterResponseDto> response = service.create(requestDto);
            return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ClaimTypeMasterRequestDto requestDto) {

        ApiResponseDTO<ClaimTypeMasterResponseDto> response = service.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ApiResponseDTO<ClaimTypeMasterResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE (IMPORTANT FOR RULE ENGINE)
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        ApiResponseDTO<ClaimTypeMasterResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {
        ApiResponseDTO<List<ClaimTypeMasterResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {
        ApiResponseDTO<List<ClaimTypeMasterResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}