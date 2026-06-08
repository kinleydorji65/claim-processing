package com.claim.claim_processing.application.controller.application;

import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimPatchRequest;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.service.application.ClaimApplicationFlowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-processing-flow/claims")
@RequiredArgsConstructor
public class ClaimApplicationFlowController {

    private final ClaimApplicationFlowService claimApplicationFlowService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> create(
            @RequestBody GeneralClaimCreateRequest request
    ) {
        return ResponseEntity.ok(claimApplicationFlowService.create(request));
    }

    @PatchMapping
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> patch(
            @RequestBody GeneralClaimPatchRequest request
    ) {
        return ResponseEntity.ok(claimApplicationFlowService.patch(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getAll() {
        return ResponseEntity.ok(claimApplicationFlowService.getAll());
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> findByApplicationId(
            @PathVariable Long applicationId
    ) {
        return ResponseEntity.ok(claimApplicationFlowService.findByApplicationId(applicationId));
    }
}
