package com.claim.claim_processing.common.controller.beneficiary;

import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.beneficiary.ClaimantTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.request.beneficiary.ClaimantTypeCreateRequestDto;
import com.claim.claim_processing.common.service.beneficiary.ClaimantTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/claimant-types")
@RequiredArgsConstructor
public class ClaimantTypeController {

    private final ClaimantTypeService claimantTypeService;

    @GetMapping
    public ResponseEntity<List<ClaimantTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(claimantTypeService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimantTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(claimantTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ClaimantTypeResponseDto> create(
            @RequestBody ClaimantTypeCreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimantTypeService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimantTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody ClaimantTypeUpdateRequestDto requestDto) {
        return ResponseEntity.ok(claimantTypeService.update(id, requestDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        claimantTypeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
