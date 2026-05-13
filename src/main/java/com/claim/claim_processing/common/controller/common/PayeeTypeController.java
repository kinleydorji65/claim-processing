package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.PayeeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.PayeeTypeResponseDto;
import com.claim.claim_processing.common.service.common.PayeeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/payee-type")
@RequiredArgsConstructor
public class PayeeTypeController {

    private final PayeeTypeService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PayeeTypeRequestDto dto
    ) {

        ApiResponseDTO<PayeeTypeResponseDto> response =
                service.create(dto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(
            @PathVariable Long id,
            @RequestBody PayeeTypeRequestDto dto
    ) {

        ApiResponseDTO<PayeeTypeResponseDto> response =
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

        ApiResponseDTO<PayeeTypeResponseDto> response =
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

        ApiResponseDTO<PayeeTypeResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<PayeeTypeResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<PayeeTypeResponseDto>> response =
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