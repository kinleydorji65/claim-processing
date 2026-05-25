package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.DisasterTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.DisasterTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.DisasterTypeUpdateDto;
import com.claim.claim_processing.common.service.partial.DisasterTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/disaster-types")
@RequiredArgsConstructor
public class DisasterTypeController {

    private final DisasterTypeService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody DisasterTypeRequestDto requestDto) {

        ApiResponseDTO<DisasterTypeResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DisasterTypeUpdateDto updateDto) {

        ApiResponseDTO<DisasterTypeResponseDto> response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<DisasterTypeResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<DisasterTypeResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<DisasterTypeResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<DisasterTypeResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}