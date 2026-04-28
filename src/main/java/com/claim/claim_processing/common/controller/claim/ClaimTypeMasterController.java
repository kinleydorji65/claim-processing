package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
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
    public ResponseEntity<ClaimTypeMasterResponseDto> create(
            @RequestBody ClaimTypeMasterRequestDto requestDto) {

        return ResponseEntity.ok(service.create(requestDto));
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ClaimTypeMasterResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimTypeMasterRequestDto requestDto) {

        return ResponseEntity.ok(service.update(id, requestDto));
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ClaimTypeMasterResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // -----------------------------
    // GET BY CODE (IMPORTANT FOR RULE ENGINE)
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<ClaimTypeMasterResponseDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<List<ClaimTypeMasterResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<List<ClaimTypeMasterResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}