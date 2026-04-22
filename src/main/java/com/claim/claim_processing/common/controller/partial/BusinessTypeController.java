package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.BusinessTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.BusinessTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.BusinessTypeUpdateDto;
import com.claim.claim_processing.common.service.partial.BusinessTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/business-types")
@RequiredArgsConstructor
public class BusinessTypeController {

    private final BusinessTypeService service;

    @PostMapping
    public ResponseEntity<BusinessTypeResponseDto> create(
            @Valid @RequestBody BusinessTypeRequestDto requestDto
    ) {
        BusinessTypeResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessTypeResponseDto> getById(@PathVariable Long id) {
        BusinessTypeResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BusinessTypeResponseDto> getByCode(@PathVariable String code) {
        BusinessTypeResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BusinessTypeResponseDto>> getAll() {
        List<BusinessTypeResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BusinessTypeResponseDto>> getAllActive() {
        List<BusinessTypeResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessTypeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody BusinessTypeUpdateDto updateDto
    ) {
        BusinessTypeResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}