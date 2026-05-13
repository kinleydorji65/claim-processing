package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DeductionTypeResponseDto;
import com.claim.claim_processing.common.service.common.DeductionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/deduction-types")
@RequiredArgsConstructor
public class DeductionTypeController {

    private final DeductionTypeService service;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody DeductionTypeRequestDto dto) {
        ApiResponseDTO<DeductionTypeResponseDto> response = service.create(dto);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ApiResponseDTO<DeductionTypeResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY CODE
    // -------------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        ApiResponseDTO<DeductionTypeResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {
        ApiResponseDTO<List<DeductionTypeResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    // -------------------------------
// PATCH UPDATE
// -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(
            @PathVariable Long id,
            @RequestBody DeductionTypeRequestDto dto
    ) {

        ApiResponseDTO<DeductionTypeResponseDto> response =
                service.patch(id, dto);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // DELETE (SOFT DELETE)
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}