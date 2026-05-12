package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.InterestMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.InterestMethodResponseDto;
import com.claim.claim_processing.common.service.common.InterestMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/interest-method")
@RequiredArgsConstructor
public class InterestMethodController {

    private final InterestMethodService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody InterestMethodRequestDto dto
    ) {

        ApiResponseDTO<InterestMethodResponseDto> response =
                service.create(dto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(
            @PathVariable Long id,
            @RequestBody InterestMethodRequestDto dto
    ) {

        ApiResponseDTO<InterestMethodResponseDto> response =
                service.patch(id, dto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<InterestMethodResponseDto> response =
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

        ApiResponseDTO<InterestMethodResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<InterestMethodResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<InterestMethodResponseDto>> response =
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