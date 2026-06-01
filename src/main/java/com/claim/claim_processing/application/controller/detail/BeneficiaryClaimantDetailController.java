package com.claim.claim_processing.application.controller.detail;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiaryClaimantRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.application.service.detail.BeneficiaryClaimantDetailService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiary-claimants")
@RequiredArgsConstructor
public class BeneficiaryClaimantDetailController {

    private final BeneficiaryClaimantDetailService beneficiaryClaimantDetailService;

    @PostMapping
    public ApiResponseDTO<BeneficiaryClaimantResponseDto> create(
            @RequestBody BeneficiaryClaimantRequestDto request) {

        return beneficiaryClaimantDetailService.create(request);
    }

    @PutMapping("/{id}")
    public ApiResponseDTO<BeneficiaryClaimantResponseDto> update(
            @PathVariable Long id,
            @RequestBody BeneficiaryClaimantRequestDto request) {

        return beneficiaryClaimantDetailService.update(id, request);
    }

    @GetMapping("/{id}")
    public ApiResponseDTO<BeneficiaryClaimantResponseDto> getById(
            @PathVariable Long id) {

        return beneficiaryClaimantDetailService.getById(id);
    }

    @GetMapping("/settlement/{beneficiarySettlementDetailId}")
    public ApiResponseDTO<List<BeneficiaryClaimantResponseDto>> getByBeneficiarySettlementDetailId(
            @PathVariable Long beneficiarySettlementDetailId) {

        return beneficiaryClaimantDetailService
                .getByBeneficiarySettlementDetailId(beneficiarySettlementDetailId);
    }

    @GetMapping
    public ApiResponseDTO<List<BeneficiaryClaimantResponseDto>> getAll() {
        return beneficiaryClaimantDetailService.getAll();
    }

    @DeleteMapping("/{id}")
    public ApiResponseDTO<Void> delete(
            @PathVariable Long id) {

        return beneficiaryClaimantDetailService.delete(id);
    }
}