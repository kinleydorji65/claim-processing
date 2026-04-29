package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ActionResponseDto;
import com.claim.claim_processing.common.service.common.ActionMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/master/action-master")
@RequiredArgsConstructor
public class ActionMasterController {

    private final ActionMasterService service;

    // -------------------------------
    // CREATE
    // -------------------------------
    @PostMapping
    public ResponseEntity<ActionResponseDto> create(
            @RequestBody ActionRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // -------------------------------
    // PATCH (PARTIAL UPDATE ONLY)
    // -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<ActionResponseDto> patch(
            @PathVariable Long id,
            @RequestBody ActionRequestDto dto) {

        dto.setId(id);
        return ResponseEntity.ok(service.patch(dto));
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ActionResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<List<ActionResponseDto>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @GetMapping("/active")
    public ResponseEntity<List<ActionResponseDto>> getAllActive() {

        return ResponseEntity.ok(service.getAllActive());
    }

    // -------------------------------
    // SOFT DELETE
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}