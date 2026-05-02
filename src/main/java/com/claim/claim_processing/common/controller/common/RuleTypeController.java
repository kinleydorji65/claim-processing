package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.RuleTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.service.common.RuleTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/common/rule-type")
@RequiredArgsConstructor
public class RuleTypeController {

    private final RuleTypeService service;

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<RuleTypeResponseDto> create(
            @RequestBody RuleTypeRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // 🔹 UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<RuleTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody RuleTypeRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<RuleTypeResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 GET BY CODE
    @GetMapping("/code/{code}")
    public ResponseEntity<RuleTypeResponseDto> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<RuleTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 GET ALL ACTIVE
    @GetMapping("/active")
    public ResponseEntity<List<RuleTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.ok("Rule Type deactivated successfully");
    }
}