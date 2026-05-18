package com.claim.claim_processing.common.controller.specialCase;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.specialCase.SpecialCaseAuthorityUpdateRequestDto;
import com.claim.claim_processing.common.service.specialCase.SpecialCaseAuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/special-case-authorities")
@RequiredArgsConstructor
public class SpecialCaseAuthorityController {

    private final SpecialCaseAuthorityService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody SpecialCaseAuthorityRequestDto requestDto) {

        ApiResponseDTO<SpecialCaseAuthorityResponseDto> response =
                service.create(requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<SpecialCaseAuthorityResponseDto> response =
                service.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<SpecialCaseAuthorityResponseDto> response =
                service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<SpecialCaseAuthorityResponseDto>> response =
                service.getAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<SpecialCaseAuthorityResponseDto>> response =
                service.getAllActive();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody SpecialCaseAuthorityUpdateRequestDto updateRequestDto) {

        ApiResponseDTO<SpecialCaseAuthorityResponseDto> response =
                service.update(id, updateRequestDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        ApiResponseDTO<String> response =
                service.delete(id);

        return ResponseEntity.ok(response);
    }
}