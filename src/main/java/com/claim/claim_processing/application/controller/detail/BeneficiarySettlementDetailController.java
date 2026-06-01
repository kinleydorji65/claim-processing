package com.claim.claim_processing.application.controller.detail;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiarySettlementDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.service.detail.BeneficiarySettlementDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/beneficiary-settlement-details")
@RequiredArgsConstructor
public class BeneficiarySettlementDetailController {

    private final BeneficiarySettlementDetailService service;

    @PostMapping
    public ResponseEntity<BeneficiarySettlementResponseDto> create(
            @RequestBody BeneficiarySettlementDetailRequestDto request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BeneficiarySettlementResponseDto> patch(
            @PathVariable Long id,
            @RequestBody BeneficiarySettlementDetailRequestDto request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiarySettlementResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/claim-application/{claimApplicationId}")
    public ResponseEntity<BeneficiarySettlementResponseDto> getByClaimApplicationId(
            @PathVariable Long claimApplicationId
    ) {
        return ResponseEntity.ok(
                service.getByClaimApplicationId(claimApplicationId)
        );
    }

    @GetMapping("/deceased-member-code/{deceasedMemberCode}")
    public ResponseEntity<BeneficiarySettlementResponseDto> getByDeceasedMemberCode(
            @PathVariable String deceasedMemberCode
    ) {
        return ResponseEntity.ok(
                service.getByDeceasedMemberCode(deceasedMemberCode)
        );
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarySettlementResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}