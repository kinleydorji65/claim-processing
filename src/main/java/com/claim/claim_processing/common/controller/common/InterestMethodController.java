package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.InterestMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.common.InterestMethodResponseDto;
import com.claim.claim_processing.common.service.common.InterestMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/interest-method")
@RequiredArgsConstructor
public class InterestMethodController {

    private final InterestMethodService service;

    // 🔹 Create
    @PostMapping
    public ResponseEntity<InterestMethodResponseDto> create(
            @RequestBody InterestMethodRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 Update
    @PatchMapping("/{id}")
    public ResponseEntity<InterestMethodResponseDto> update(
            @PathVariable Long id,
            @RequestBody InterestMethodRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 Get By ID
    @GetMapping("/{id}")
    public ResponseEntity<InterestMethodResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 Get By Code
    @GetMapping("/code/{code}")
    public ResponseEntity<InterestMethodResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // 🔹 Get All
    @GetMapping
    public ResponseEntity<List<InterestMethodResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 Get All Active
    @GetMapping("/active")
    public ResponseEntity<List<InterestMethodResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Interest Method deactivated successfully.");
    }
}