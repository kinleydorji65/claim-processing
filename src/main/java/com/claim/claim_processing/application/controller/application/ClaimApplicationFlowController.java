package com.claim.claim_processing.application.controller.application;

import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimPatchRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.service.application.ClaimApplicationFlowService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// REMOVE: import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/approved")
    @Operation(summary = "Get all Approved Detail")
    public ApiResponseDTO<Page<GeneralClaimDetailResponse>> getAllApprovedDetails(
            @PageableDefault(size = 20) Pageable pageable) {
        
        return claimApplicationApprovalService.getAllApprovedDetails(pageable);
    }
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
    @GetMapping("/claims/{applicationNumber}/workflow-history")
    public ResponseEntity<ApiResponseDTO<List<ClaimApplicationWorkflowResponseDto>>> getWorkflowDetails(
            @PathVariable String applicationNumber) {
        return ResponseEntity.ok(claimApplicationFlowService.getWorkflowDetails(applicationNumber));
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

    // Approval Endpoints
    @PatchMapping("/claims/{applicationNumber}/reject")
    @Operation(summary = "Reject action by verified")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> rejectbyVerifier(
            @PathVariable String applicationNumber,
            @RequestBody ClaimApplicationVerificationRequestDto request) {
        return ResponseEntity.ok(
                claimApplicationFlowService.rejectedClaimApplication(applicationNumber, request));
    }

    @PatchMapping("/claims/{applicationNumber}/approval/approve")
    @Operation(summary = "Approve action for approver")
    public ResponseEntity<ApiResponseDTO<GeneralClaimDetailResponse>> approveApproval(
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

    @PatchMapping("/claims/{applicationNumber}/verification/verify")
    @Operation(summary = "Verify action by verifier")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> verifyVerification(
            @PathVariable String applicationNumber,
            @RequestBody ClaimApplicationVerificationRequestDto request) {
        return ResponseEntity.ok(
                claimApplicationVerificationService.verify(applicationNumber, request));
    }

    @PatchMapping("/claims/{applicationNumber}/verification/reject")
    @Operation(summary = "Reject a claim application by verifier")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> rejectedClaimApplication(@PathVariable String applicationNumber, @RequestBody ClaimApplicationVerificationRequestDto request) {
        return ResponseEntity.ok(
                claimApplicationVerificationService.rejectedClaimApplication(applicationNumber, request));
    }

    @GetMapping("/claims/{applicationNumber}/verification")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationVerificationResponseDto>> getVerificationByApplicationNumber(
            @PathVariable String applicationNumber) {
        return ResponseEntity.ok(
                claimApplicationVerificationService.getByApplicationNumber(applicationNumber));
    }
    @GetMapping("/claims/{agencyCode}/agencyCode/{claimTypeId}")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getByAgencyCode(
            @PathVariable String agencyCode, @PathVariable Long claimTypeId) {
        return ResponseEntity.ok(
                claimApplicationFlowService.getByAgencyCodeAndClaimTypeId(agencyCode, claimTypeId));
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

    @GetMapping("/claims/user/{userCode}/status/{statusId}")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getClaimsByUserCode(
            @PathVariable String userCode, @PathVariable Long statusId) {
        return ResponseEntity.ok(
                claimApplicationFlowService.findByUserCode(userCode, statusId));
    }
    @GetMapping("/claims/legal-recovery/user/{userCode}")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getLegalRecoveryWithUserCode(
            @PathVariable String userCode) {
        return ResponseEntity.ok(
                claimApplicationFlowService.getLegalRecoveryWithUserCode(userCode));
    }

    @GetMapping("/claims/get-all-verified-claim")
    @Operation(summary = "Get all verified claim application for the approval")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getVerifiedClaim() {
        return ResponseEntity.ok(
                claimApplicationFlowService.getVerifiedClaim());
    }

//     @GetMapping("/claims/verified-application")
//     @Operation(summary = "Get all verified claim application")
//     public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getAllVerifiedApplication() {
//         return ResponseEntity.ok(
//                 claimApplicationFlowService.getVerifiedApplication());
//     }

    @PatchMapping("/claims/verified-application-claimed-by")
    @Operation(summary = "Claim by for the verifier")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> verifiedClaimApplicationClaimedBy(
            @RequestParam String applicationNumber,
            @RequestParam String claimedBy) {
        return ResponseEntity.ok(
                claimApplicationFlowService.verifiedClaimApplicationClaimedBy(applicationNumber, claimedBy));
    }

    @PatchMapping("/claims/verified-application-unclaimed-by")
    @Operation(summary = "Unclaim for the verifier")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> verifiedClaimApplicationUnClaimedBy(
            @RequestParam String applicationNumber,
            @RequestParam String unClaimedBy) {
        return ResponseEntity.ok(
                claimApplicationFlowService.verifiedClaimApplicationUnClaimedBy(applicationNumber, unClaimedBy));
    }

    @PatchMapping("/claims/{applicationNumber}/approval/claim")
    @Operation(summary = "Claim by action for the approver")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationApprovalResponseDto>> verifiedClaimActionClaimedBy(
            @PathVariable String applicationNumber,
            @RequestParam String claimedBy) {
        return ResponseEntity.ok(
                claimApplicationApprovalService.verifiedClaimActionClaimedBy(applicationNumber, claimedBy));
    }

    @PatchMapping("/claims/{applicationNumber}/approval/unclaim")
    @Operation(summary = "Unclaim action for the approver")
    public ResponseEntity<ApiResponseDTO<ClaimApplicationApprovalResponseDto>> verifiedClaimActionUnClaimedBy(
            @PathVariable String applicationNumber,
            @RequestParam String unClaimedBy) {
        return ResponseEntity.ok(
                claimApplicationApprovalService.verifiedClaimActionUnClaimedBy(applicationNumber, unClaimedBy));
    }

    @PatchMapping("/claims/{applicationNumber}/approval/reject")
    @Operation(summary = "Reject action for the approver")
    public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> verifiedClaimActionRejectedByApprover(
            @PathVariable String applicationNumber,
            @RequestParam String rejectedBy,
            @RequestParam String rejectedRemarks) {
        return ResponseEntity.ok(
                claimApplicationFlowService.verifiedClaimActionRejectedByApprover(applicationNumber, rejectedBy, rejectedRemarks));
    }

    @PatchMapping("/claims/{applicationNumber}/mark-as-special")
    @Operation(summary = "Mark as special action for the approver")
    public ResponseEntity<ApiResponseDTO<GeneralClaimDetailResponse>> markAsSpecial(
            @PathVariable String applicationNumber,
            @RequestParam String updatedBy,
            @RequestParam String remarks) {
        return ResponseEntity.ok(
                claimApplicationApprovalService.markAsSpecial(applicationNumber, updatedBy, remarks));
    }

    @GetMapping("/claims/get-verified-claim-rejected-by-approver")
    @Operation(summary = "Get all verified claim application which is rejected by approver")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getVerifiedClaimButRejectedClaim() {
        return ResponseEntity.ok(
                claimApplicationFlowService.getVerifiedClaimButRejectedClaim());
    }

    @GetMapping("/claims/get-verified-claim-with-claimed-by/{claimedBy}")
    @Operation(summary = "Get all verified claim application which is claimed by approver user")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getVerifiedClaimAndClaimedBy(@PathVariable String claimedBy) {
        return ResponseEntity.ok(
                claimApplicationFlowService.getVerifiedClaimAndClaimedBy(claimedBy));
    }

    @GetMapping("/claims/get-claim-application-with-claimed-by/{claimedBy}")
    @Operation(summary = "Get all claim application which is claimed by a verifier user")
    public ResponseEntity<ApiResponseDTO<List<GeneralClaimResponse>>> getVerifiedClaimWhichClaimedBy(@PathVariable String claimedBy) {
        return ResponseEntity.ok(
                claimApplicationFlowService.getClaimApplicationWhichClaimedBy(claimedBy));
    }
    
}