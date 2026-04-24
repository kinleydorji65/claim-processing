package com.claim.claim_processing.common.controller.others;

import com.claim.claim_processing.common.DTO.request.others.NppfOfficeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.NppfOfficeResponseDto;
import com.claim.claim_processing.common.DTO.update.others.NppfOfficeUpdateDto;
import com.claim.claim_processing.common.service.others.NppfOfficeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Other Masters", description = "NPPF office master APIs")
@RestController
@RequestMapping("/api/claim/masters/nppf-offices")
@RequiredArgsConstructor
public class NppfOfficeController {

    private final NppfOfficeService service;

    @PostMapping
    public ResponseEntity<NppfOfficeResponseDto> create(
            @Valid @RequestBody NppfOfficeRequestDto requestDto
    ) {
        NppfOfficeResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NppfOfficeResponseDto> getById(@PathVariable Long id) {
        NppfOfficeResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<NppfOfficeResponseDto> getByCode(@PathVariable Long code) {
        NppfOfficeResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NppfOfficeResponseDto>> getAll() {
        List<NppfOfficeResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<NppfOfficeResponseDto>> getAllActive() {
        List<NppfOfficeResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NppfOfficeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody NppfOfficeUpdateDto updateDto
    ) {
        NppfOfficeResponseDto response = service.update(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}