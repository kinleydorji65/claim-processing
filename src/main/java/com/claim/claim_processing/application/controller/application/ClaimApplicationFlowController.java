package com.claim.claim_processing.application.controller.application;

import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimPatchRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.service.application.ClaimApplicationFlowService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-processing-flow")
@RequiredArgsConstructor
public class ClaimApplicationFlowController {

    private final ClaimApplicationFlowService claimApplicationFlowService;
    private final ClaimApplicationApprovalService claimApplicationApprovalService;
    private final ClaimApplicationVerificationService claimApplicationVerificationService;

    // Claim endpoints
    @PostMapping("/claims")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> create(
            @RequestBody GeneralClaimCreateRequest request) {
        return ResponseEntity.ok(claimApplicationFlowService.create(request));
    }

    @PatchMapping("/claims")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> patch(
            @RequestBody GeneralClaimPatchRequest request) {
        return ResponseEntity.ok(claimApplicationFlowService.patch(request));
    }

    @GetMapping("/claims")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getAll() {
        return ResponseEntity.ok(claimApplicationFlowService.getAll());
    }

    @GetMapping("/claims/{applicationNumber}")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> findByApplicationNumber(
            @PathVariable String applicationNumber) {
        return ResponseEntity.ok(claimApplicationFlowService.findByApplicationNumber(applicationNumber));
    }

    @GetMapping("/claims/nppf/{nppfNumber}")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> findByNppfNumber(
            @PathVariable String nppfNumber) {
        return ResponseEntity.ok(claimApplicationFlowService.findByNppfNumber(nppfNumber));
    }

    @GetMapping("/claims/get-verified-applications")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getVerifiedApplication() {
        return ResponseEntity.ok(claimApplicationFlowService.getVerifiedApplication());
    }

    // Approval Endpoints
    @PatchMapping("/claims/{applicationNumber}/approval")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationApprovalResponseDto>> patchApproval(
            @PathVariable String applicationNumber,
            @RequestBody ClaimApplicationApprovalRequestDto request) {
        return ResponseEntity.ok(
                claimApplicationApprovalService.patch(applicationNumber, request));
    }

    @PostMapping("/claims/{applicationNumber}/approval/approve")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationApprovalResponseDto>> approveApproval(
            @PathVariable String applicationNumber,
            @RequestBody ClaimApplicationApprovalRequestDto request) {
        return ResponseEntity.ok(
                claimApplicationApprovalService.approve(applicationNumber, request));
    }

    @GetMapping("/claims/{applicationNumber}/approval")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationApprovalResponseDto>> getApprovalByApplicationNumber(
            @PathVariable String applicationNumber) {
        return ResponseEntity.ok(
                claimApplicationApprovalService.getByApplicationNumber(applicationNumber));
    }

    // Verification Endpoints
    @PatchMapping("/claims/{applicationNumber}/verification")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> patchVerification(
            @PathVariable String applicationNumber,
            @RequestBody ClaimApplicationVerificationRequestDto request) {
        return ResponseEntity.ok(
                claimApplicationVerificationService.patch(applicationNumber, request));
    }

    @PostMapping("/claims/{applicationNumber}/verification/verify")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> verifyVerification(
            @PathVariable String applicationNumber,
            @RequestBody ClaimApplicationVerificationRequestDto request) {
        return ResponseEntity.ok(
                claimApplicationVerificationService.verify(applicationNumber, request));
    }

    @GetMapping("/claims/{applicationNumber}/verification")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> getVerificationByApplicationNumber(
            @PathVariable String applicationNumber) {
        return ResponseEntity.ok(
                claimApplicationVerificationService.getByApplicationNumber(applicationNumber));
    }

    @PatchMapping("/claims/{applicationNumber}/claim")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> claimApplication(
            @PathVariable String applicationNumber,
            @RequestParam String claimedBy) {
        return ResponseEntity.ok(
                claimApplicationFlowService.claimedBy(applicationNumber, claimedBy));
    }

    @PatchMapping("/claims/{applicationNumber}/unclaim")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> unClaimApplication(
            @PathVariable String applicationNumber,
            @RequestParam String unclaimedBy) {
        return ResponseEntity.ok(
                claimApplicationFlowService.unClaimedBy(applicationNumber, unclaimedBy));
    }

    @GetMapping("/claims/user/{userCode}")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getClaimsByUserCode(
            @PathVariable String userCode) {
        return ResponseEntity.ok(
                claimApplicationFlowService.findByUserCode(userCode));
    }

    @PatchMapping("/claims/verified-application-claimed-by")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> verifiedClaimApplicationClaimedBy(
            @RequestParam String applicationNumber,
            @RequestParam String claimedBy) {
        return ResponseEntity.ok(
                claimApplicationFlowService.verifiedClaimApplicationClaimedBy(applicationNumber, claimedBy));
    }
}