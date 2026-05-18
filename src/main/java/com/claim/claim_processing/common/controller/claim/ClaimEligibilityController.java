package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.ClaimEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/claim-eligibilities")
@RequiredArgsConstructor
public class ClaimEligibilityController {

    private final ClaimEligibilityService claimEligibilityService;

    @GetMapping
    public ResponseEntity<?> getAllActive() {
        ApiResponseDTO<List<ClaimEligibilityResponseDto>> response = claimEligibilityService.getAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ApiResponseDTO<ClaimEligibilityResponseDto> response = claimEligibilityService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ClaimEligibilityCreateRequestDto requestDto) {
        ApiResponseDTO<ClaimEligibilityResponseDto> response = claimEligibilityService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ClaimEligibilityUpdateRequestDto requestDto) {
        ApiResponseDTO<ClaimEligibilityResponseDto> response = claimEligibilityService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        ApiResponseDTO<String> response = claimEligibilityService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-circumstance/{id}")
    public ResponseEntity<?> getByCircumstance(@PathVariable Long id) {
        ApiResponseDTO<List<ClaimEligibilityResponseDto>> response = claimEligibilityService.getByClaimCircumstanceId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-scheme/{id}")
    public ResponseEntity<?> getByScheme(@PathVariable Long id) {
        ApiResponseDTO<List<ClaimEligibilityResponseDto>> response = claimEligibilityService.getBySchemeTypeId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-rule-type/{id}")
    public ResponseEntity<?> getByRuleType(@PathVariable Long id) {
        ApiResponseDTO<List<ClaimEligibilityResponseDto>> response = claimEligibilityService.getByRuleTypeId(id);
        return ResponseEntity.ok(response);
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {

        ApiResponseDTO<List<ClaimEligibilityResponseDto>> response =
                claimEligibilityService.getAll();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        ApiResponseDTO<String> response =
                claimEligibilityService.delete(id);

        return ResponseEntity.ok(response);
    }
}