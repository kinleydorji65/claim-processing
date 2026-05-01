package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.PayeeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.PayeeTypeResponseDto;
import com.claim.claim_processing.common.service.common.PayeeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/payee-type")
@RequiredArgsConstructor
public class PayeeTypeController {

    private final PayeeTypeService service;

    // 🔹 Create
    @PostMapping
    public ResponseEntity<PayeeTypeResponseDto> create(
            @RequestBody PayeeTypeRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 Update (PATCH style)
    @PatchMapping("/{id}")
    public ResponseEntity<PayeeTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody PayeeTypeRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 Get By ID
    @GetMapping("/{id}")
    public ResponseEntity<PayeeTypeResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 Get By Code
    @GetMapping("/code/{code}")
    public ResponseEntity<PayeeTypeResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // 🔹 Get All
    @GetMapping
    public ResponseEntity<List<PayeeTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 Get All Active
    @GetMapping("/active")
    public ResponseEntity<List<PayeeTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Payee Type deactivated successfully.");
    }
}