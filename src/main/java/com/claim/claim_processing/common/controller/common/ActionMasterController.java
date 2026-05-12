package com.claim.claim_processing.common.controller.common;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
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
    public ResponseEntity<?> create(
            @RequestBody ActionRequestDto dto
    ) {

        ApiResponseDTO<ActionResponseDto> response =
                service.create(dto);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // PATCH (PARTIAL UPDATE ONLY)
    // -------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(
            @PathVariable Long id,
            @RequestBody ActionRequestDto dto
    ) {

        dto.setId(id);

        ApiResponseDTO<ActionResponseDto> response =
                service.patch(dto);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET BY ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {

        ApiResponseDTO<ActionResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET ALL
    // -------------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<ActionResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // GET ALL ACTIVE
    // -------------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<ActionResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // SOFT DELETE
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}