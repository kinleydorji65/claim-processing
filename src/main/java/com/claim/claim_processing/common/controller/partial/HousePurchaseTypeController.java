package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.HousePurchaseTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.HousePurchaseTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.HousePurchaseTypeUpdateDto;
import com.claim.claim_processing.common.service.partial.HousePurchaseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/house-purchase-types")
@RequiredArgsConstructor
public class HousePurchaseTypeController {

    private final HousePurchaseTypeService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody HousePurchaseTypeRequestDto requestDto) {

        ApiResponseDTO<HousePurchaseTypeResponseDto> response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        ApiResponseDTO<HousePurchaseTypeResponseDto> response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {

        ApiResponseDTO<HousePurchaseTypeResponseDto> response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<HousePurchaseTypeResponseDto>> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<HousePurchaseTypeResponseDto>> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody HousePurchaseTypeUpdateDto updateDto) {

        ApiResponseDTO<HousePurchaseTypeResponseDto> response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response = service.delete(id);
        return ResponseEntity.ok(response);
    }
}