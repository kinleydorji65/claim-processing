package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.ReviewStatusRequestDto;
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

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<ReviewStatusResponseDto> create(@RequestBody ReviewStatusRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewStatusResponseDto> update(
            @PathVariable Long id,
            @RequestBody ReviewStatusRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewStatusResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 GET BY CODE
    @GetMapping("/code/{code}")
    public ResponseEntity<ReviewStatusResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<ReviewStatusResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 GET ACTIVE
    @GetMapping("/active")
    public ResponseEntity<List<ReviewStatusResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 DELETE (SOFT DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Review Status deactivated successfully");
    }
}