package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.BusinessTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.BusinessTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.BusinessTypeUpdateDto;
import com.claim.claim_processing.common.service.partial.BusinessTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/partial/business-types")
@RequiredArgsConstructor
public class BusinessTypeController {

    private final BusinessTypeService service;

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody BusinessTypeRequestDto requestDto) {

        ApiResponseDTO<BusinessTypeResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody BusinessTypeUpdateDto updateDto) {

        ApiResponseDTO<BusinessTypeResponseDto> response =
                service.update(id, updateDto);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<BusinessTypeResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET BY CODE
    // -----------------------------
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<BusinessTypeResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<BusinessTypeResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // GET ALL ACTIVE
    // -----------------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<BusinessTypeResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}