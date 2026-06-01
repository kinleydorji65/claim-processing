package com.claim.claim_processing.application.controller.detail;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemittanceDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemittanceResponseDto;
import com.claim.claim_processing.application.service.detail.WrongRemittanceDetailService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/wrong-remittance-details")
@RequiredArgsConstructor
public class WrongRemittanceDetailController {

    private final WrongRemittanceDetailService wrongRemittanceDetailService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<WrongRemittanceResponseDto>> create(
            @RequestBody WrongRemittanceDetailRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(wrongRemittanceDetailService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<WrongRemittanceResponseDto>> update(
            @PathVariable Long id,
            @RequestBody WrongRemittanceDetailRequestDto request
    ) {
        return ResponseEntity.ok(
                wrongRemittanceDetailService.update(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<WrongRemittanceResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                wrongRemittanceDetailService.getById(id)
        );
    }

    @GetMapping("/claim-application/{claimApplicationId}")
    public ResponseEntity<ApiResponseDTO<WrongRemittanceResponseDto>> getByClaimApplicationId(
            @PathVariable Long claimApplicationId
    ) {
        return ResponseEntity.ok(
                wrongRemittanceDetailService.getByClaimApplicationId(claimApplicationId)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<WrongRemittanceResponseDto>>> getAll() {
        return ResponseEntity.ok(
                wrongRemittanceDetailService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                wrongRemittanceDetailService.delete(id)
        );
    }

    @GetMapping("/agency-code/{agencyCode}")
    public ResponseEntity<ApiResponseDTO<List<WrongRemittanceResponseDto>>> getByAgencyCode(
            @PathVariable String agencyCode
    ) {
        return ResponseEntity.ok(
                wrongRemittanceDetailService.getByAgencyCode(agencyCode)
        );
    }
}