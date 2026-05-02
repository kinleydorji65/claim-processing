package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.StageRequestDto;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.service.common.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/stage")
@RequiredArgsConstructor
public class StageController {

    private final StageService service;

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<StageResponseDto> create(
            @RequestBody StageRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<StageResponseDto> update(
            @PathVariable Long id,
            @RequestBody StageRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<StageResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 GET BY CODE
    @GetMapping("/code/{code}")
    public ResponseEntity<StageResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<StageResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 GET ALL ACTIVE
    @GetMapping("/active")
    public ResponseEntity<List<StageResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Stage deactivated successfully");
    }
}