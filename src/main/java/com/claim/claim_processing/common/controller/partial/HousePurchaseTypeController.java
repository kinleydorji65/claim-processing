package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.HousePurchaseTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.HousePurchaseTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.HousePurchaseTypeUpdateDto;
import com.claim.claim_processing.common.service.partial.HousePurchaseTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/house-purchase-types")
@RequiredArgsConstructor
public class HousePurchaseTypeController {

    private final HousePurchaseTypeService service;

    @PostMapping
    public ResponseEntity<HousePurchaseTypeResponseDto> create(
            @Valid @RequestBody HousePurchaseTypeRequestDto requestDto
    ) {
        HousePurchaseTypeResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HousePurchaseTypeResponseDto> getById(@PathVariable Long id) {
        HousePurchaseTypeResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<HousePurchaseTypeResponseDto> getByCode(@PathVariable String code) {
        HousePurchaseTypeResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<HousePurchaseTypeResponseDto>> getAll() {
        List<HousePurchaseTypeResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<HousePurchaseTypeResponseDto>> getAllActive() {
        List<HousePurchaseTypeResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HousePurchaseTypeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody HousePurchaseTypeUpdateDto updateDto
    ) {
        HousePurchaseTypeResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}