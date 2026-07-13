package com.claim.claim_processing.application.controller.application;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.request.application.GeneralClaimCreateRequest;
import com.claim.claim_processing.application.DTO.request.application.GeneralSpecialCaseApplicationRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.service.application.ClaimApplicationFlowService;
import com.claim.claim_processing.application.service.specialCase.SpecialCaseWorkFlowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-application/special-case")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Special Case Workflow", description = "APIs for managing special case claim workflows")
public class SpecialCaseWorkFlowController {

        private final SpecialCaseWorkFlowService specialCaseWorkFlowService;
        private final ClaimApplicationFlowService claimApplicationFlowService;

        @GetMapping("/approved")
    @Operation(summary = "Get all special case Approved Detail")
    public ApiResponseDTO<Page<GeneralSpecialCaseResponse>> getAllApprovedSpecialCases(Pageable pageable) {
        
        return specialCaseWorkFlowService.getAllApprovedSpecialCases(pageable);
    }

        @PostMapping("/claims")
        public ResponseEntity<ApiResponseDTO<GeneralClaimResponse>> create(
                        @RequestBody GeneralClaimCreateRequest request) {
                return ResponseEntity.ok(claimApplicationFlowService.create(request));
        }
        

        @PostMapping("/create")
        @Operation(summary = "Create a new special case with application", description = "Creates a new special case application with the provided details")
        public ResponseEntity<ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO>> createSpecialCaseWithApplication(
                        @Valid @RequestBody GeneralSpecialCaseApplicationRequest request) {
                log.info("Creating new special case with application");
                ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> response = specialCaseWorkFlowService
                                .createSpecialCaseWithApplication(request);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @PatchMapping("/{applicationNumber}/approve")
        @Operation(summary = "Approve a special case application", description = "Approves a special case application with the given application number")
        public ResponseEntity<ApiResponseDTO<GeneralSpecialCaseResponse>> approveSpecialCase(
                        @Parameter(description = "Application number of the special case") @PathVariable String applicationNumber,
                        @Valid @RequestBody ClaimApplicationApprovalRequestDto request) {
                log.info("Approving special case with application number: {}", applicationNumber);
                ApiResponseDTO<GeneralSpecialCaseResponse> response = specialCaseWorkFlowService
                                .approveSpecialCase(applicationNumber, request);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{applicationNumber}/patch")
        @Operation(summary = "Patch an existing special case application", description = "Partially updates an existing special case application")
        public ResponseEntity<ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO>> patchSpecialCaseWithApplication(
                        @Valid @RequestBody GeneralSpecialCaseApplicationRequest request) {
                log.info("Patching special case application");
                ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> response = specialCaseWorkFlowService
                                .patchSpecialCaseWithApplication(request);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{applicationNumber}/reject")
        @Operation(summary = "Reject a special case application", description = "Rejects a special case application with provided remarks")
        public ResponseEntity<ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO>> rejectSpecialCase(
                        @Parameter(description = "Application number of the special case") @PathVariable String applicationNumber,
                        @RequestParam String rejectedBy,
                        @RequestParam String rejectedRemarks) {
                log.info("Rejecting special case with application number: {} by: {}", applicationNumber, rejectedBy);
                ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> response = specialCaseWorkFlowService
                                .rejectSpecialCase(applicationNumber, rejectedBy, rejectedRemarks);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{applicationNumber}/claim")
        @Operation(summary = "Claim a special case application", description = "Claims a special case application for processing by a specific user")
        public ResponseEntity<ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO>> verifiedClaimActionClaimedBy(
                        @Parameter(description = "Application number of the special case") @PathVariable String applicationNumber,
                        @RequestParam String claimedBy) {
                log.info("Claiming special case with application number: {} by: {}", applicationNumber, claimedBy);
                ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> response = specialCaseWorkFlowService
                                .verifiedClaimActionClaimedBy(applicationNumber, claimedBy);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{applicationNumber}/unclaim")
        @Operation(summary = "Unclaim a special case application", description = "Unclaims a special case application that was previously claimed")
        public ResponseEntity<ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO>> verifiedClaimActionUnClaimedBy(
                        @Parameter(description = "Application number of the special case") @PathVariable String applicationNumber,
                        @RequestParam String unClaimedBy) {
                log.info("Unclaiming special case with application number: {} by: {}", applicationNumber, unClaimedBy);
                ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> response = specialCaseWorkFlowService
                                .verifiedClaimActionUnClaimedBy(applicationNumber, unClaimedBy);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/user/{userCode}")
        @Operation(summary = "Get special cases by user code", description = "Retrieves all special case applications associated with a specific user code")
        public ResponseEntity<ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>>> getSpecialCaseUserCode(
                        @Parameter(description = "User code to filter special cases") @PathVariable String userCode) {
                log.info("Fetching special cases for user code: {}", userCode);
                ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> response = specialCaseWorkFlowService
                                .getSpecialCaseUserCode(userCode);
                return ResponseEntity.ok(response);
        }

        @GetMapping()
        public ResponseEntity<ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>>> getAllSpecialCase() {
                ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> response = specialCaseWorkFlowService
                                .getAllSpecialCase();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/special-case/claimedBy")
        public ResponseEntity<ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>>> getAllSpecialCaseWithClaimedBy(
                        @RequestParam String claimedBy) {
                ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> response = specialCaseWorkFlowService
                                .getAllSpecialCaseWithClaimedBy(claimedBy);
                return ResponseEntity.ok(response);
        }

}
