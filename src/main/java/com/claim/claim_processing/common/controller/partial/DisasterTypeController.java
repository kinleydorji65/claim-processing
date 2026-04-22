package com.claim.claim_processing.common.controller.partial;

import com.claim.claim_processing.common.DTO.request.partial.DisasterTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.DisasterTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.DisasterTypeUpdateDto;
import com.claim.claim_processing.common.service.partial.DisasterTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/disaster-types")
@RequiredArgsConstructor
public class DisasterTypeController {

    private final DisasterTypeService service;

    @PostMapping
    public ResponseEntity<DisasterTypeResponseDto> create(
            @Valid @RequestBody DisasterTypeRequestDto requestDto
    ) {
        DisasterTypeResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisasterTypeResponseDto> getById(@PathVariable Long id) {
        DisasterTypeResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DisasterTypeResponseDto> getByCode(@PathVariable String code) {
        DisasterTypeResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DisasterTypeResponseDto>> getAll() {
        List<DisasterTypeResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DisasterTypeResponseDto>> getAllActive() {
        List<DisasterTypeResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisasterTypeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody DisasterTypeUpdateDto updateDto
    ) {
        DisasterTypeResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}