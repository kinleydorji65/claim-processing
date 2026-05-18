package com.claim.claim_processing.common.controller.beneficiary;

import com.claim.claim_processing.common.DTO.request.beneficiary.ClaimantTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.beneficiary.ClaimantTypeUpdateRequestDto;
import com.claim.claim_processing.common.service.beneficiary.ClaimantTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/claimant-types")
@RequiredArgsConstructor
public class ClaimantTypeController {

    private final ClaimantTypeService claimantTypeService;

    @GetMapping
    public ResponseEntity<?> getAllActive() {

        ApiResponseDTO<List<ClaimantTypeResponseDto>> response =
                claimantTypeService.getAllActive();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        ApiResponseDTO<ClaimantTypeResponseDto> response =
                claimantTypeService.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getByCode(
            @PathVariable String code) {

        ApiResponseDTO<ClaimantTypeResponseDto> response =
                claimantTypeService.getByCode(code);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ClaimantTypeCreateRequestDto requestDto) {

        ApiResponseDTO<ClaimantTypeResponseDto> response =
                claimantTypeService.create(requestDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ClaimantTypeUpdateRequestDto requestDto) {

        ApiResponseDTO<ClaimantTypeResponseDto> response =
                claimantTypeService.update(id, requestDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        ApiResponseDTO<String> response =
                claimantTypeService.delete(id);

        return ResponseEntity.ok(response);
    }
}