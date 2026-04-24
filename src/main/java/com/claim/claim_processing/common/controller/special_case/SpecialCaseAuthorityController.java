package com.claim.claim_processing.common.controller.special_case;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.specialCase.SpecialCaseAuthorityUpdateRequestDto;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseAuthorityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/special-case-authorities")
@RequiredArgsConstructor
public class SpecialCaseAuthorityController {

    private final SpecialCaseAuthorityService service;

    @PostMapping
    public ResponseEntity<SpecialCaseAuthorityResponseDto> create(
            @Valid @RequestBody SpecialCaseAuthorityRequestDto requestDto
    ) {
        SpecialCaseAuthorityResponseDto response = service.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialCaseAuthorityResponseDto> getById(@PathVariable Long id) {
        SpecialCaseAuthorityResponseDto response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SpecialCaseAuthorityResponseDto> getByCode(@PathVariable String code) {
        SpecialCaseAuthorityResponseDto response = service.getByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SpecialCaseAuthorityResponseDto>> getAll() {
        List<SpecialCaseAuthorityResponseDto> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SpecialCaseAuthorityResponseDto>> getAllActive() {
        List<SpecialCaseAuthorityResponseDto> response = service.getAllActive();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialCaseAuthorityResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody SpecialCaseAuthorityUpdateRequestDto updateRequestDto
    ) {
        SpecialCaseAuthorityResponseDto response = service.update(id, updateRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}