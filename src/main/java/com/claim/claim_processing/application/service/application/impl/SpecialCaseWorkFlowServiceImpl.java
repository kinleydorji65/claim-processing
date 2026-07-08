package com.claim.claim_processing.application.service.application.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.request.application.GeneralSpecialCaseApplicationRequest;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.application.mapper.application.SpecialCaseApplicationGeneralResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationBankResponseMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationBankDetailService;
import com.claim.claim_processing.application.service.application.ClaimApplicationService;
import com.claim.claim_processing.application.service.application.ClaimSpecialCaseApplicationService;
import com.claim.claim_processing.application.service.application.SpecialCaseLedgerService;
import com.claim.claim_processing.application.service.application.SpecialCaseWorkFlowService;
import com.claim.claim_processing.application.service.claimDetail.SpecialCaseService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialCaseWorkFlowServiceImpl implements SpecialCaseWorkFlowService {
        private final ClaimApplicationService claimApplicationService;
        private final ClaimApplicationRepository claimApplicationRepository;
        private final ClaimApplicationWorkflowService claimApplicationWorkflowService;
        private final ClaimSpecialCaseApplicationService claimSpecialCaseApplicationService;
        private final SpecialCaseApplicationGeneralResponseMapper specialCaseGeneralResponseMapper;
        private final ClaimApplicationBankDetailService claimApplicationBankDetailService;
        private final StatusMasterRepository statusRepository;

        private final ClaimApplicationVerificationService claimApplicationVerificationService;
        private final ClaimApplicationApprovalService claimApplicationApprovalService;

        private final ClaimApplicationBankResponseMapper claimApplicationBankResponseMapper;
        private final SpecialCaseService specialCaseService;
        private final SpecialCaseLedgerService specialCaseLedgerService;

        @Override
        @Transactional
        public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> createSpecialCaseWithApplication(
                        GeneralSpecialCaseApplicationRequest request) {
                // Implementation logic for creating a special case with application
                if (request == null) {
                        throw ClaimException.badRequest("Request body is required");
                }

                ClaimApplication claimApplication = claimApplicationService.create(request.getClaimApplication());
                claimApplication.setStatus(getStatusById(41L));
                claimApplicationRepository.save(claimApplication);
                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .create(request.getClaimSpecialCaseApplicationRequestDto(), claimApplication);
                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService.create(
                                claimApplication,
                                List.of(request.getBankDetail()));

                ClaimApplicationVerificationRequestDto verificationRequest = ClaimApplicationVerificationRequestDto
                                .builder()
                                .verificationStatusId(41L)
                                .requiresRecalculation(ActivityEnum.N)
                                .requiresManualReview(ActivityEnum.N)
                                .verifiedBy(claimApplication.getCreatedBy())
                                .verifiedByRoleId(null)
                                .createdBy(claimApplication.getCreatedBy())
                                .build();
                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .patch(claimApplication.getApplicationNumber(), verificationRequest);
                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                .builder()
                                .fromStageId(request.getClaimApplication().getFromStageId())
                                .toStageId(request.getClaimApplication().getToStageId())
                                .fromStatusId(request.getClaimApplication().getFromStatusId())
                                .toStatusId(request.getClaimApplication().getToStatusId())
                                .reason(request.getClaimApplication().getReason())
                                .actionBy(request.getClaimApplication().getCreatedBy())
                                .build();
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .create(claimApplication, workflowRequest);
                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);
                responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                responseDTO.setVerificationDetail(claimVerifier.getData());
                responseDTO.setWorkflowDetails(workflowDetails);
                responseDTO.setBankDetail(bankDetails.stream()
                                .map(claimApplicationBankResponseMapper::toResponse)
                                .findFirst()
                                .orElse(null));
                return ApiResponseDTO.success(responseDTO);
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> patchSpecialCaseWithApplication(
                        GeneralSpecialCaseApplicationRequest request) {
                // Implementation logic for creating a special case with application
                if (request == null) {
                        throw ClaimException.badRequest("Request body is required");
                }

                ClaimApplication claimApplication = claimApplicationService.update(request.getClaimApplication());
                claimApplication.setStatus(getStatusById(41L));
                claimApplicationRepository.save(claimApplication);
                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .patch(request.getClaimSpecialCaseApplicationRequestDto(), claimApplication);
                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService.patch(claimApplication,
                                List.of(request.getBankDetail()));

                ClaimApplicationVerificationRequestDto verificationRequest = ClaimApplicationVerificationRequestDto
                                .builder()
                                .verificationStatusId(41L)
                                .requiresRecalculation(ActivityEnum.N)
                                .requiresManualReview(ActivityEnum.N)
                                .verifiedBy(claimApplication.getCreatedBy())
                                .verifiedByRoleId(null)
                                .createdBy(claimApplication.getCreatedBy())
                                .build();

                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .patch(claimApplication.getApplicationNumber(), verificationRequest);
                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                .builder()
                                .fromStageId(request.getClaimApplication().getFromStageId())
                                .toStageId(request.getClaimApplication().getToStageId())
                                .fromStatusId(request.getClaimApplication().getFromStatusId())
                                .toStatusId(request.getClaimApplication().getToStatusId())
                                .reason(request.getClaimApplication().getReason())
                                .actionBy(request.getClaimApplication().getCreatedBy())
                                .build();
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .create(claimApplication, workflowRequest);
                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);
                responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                responseDTO.setVerificationDetail(claimVerifier.getData());
                responseDTO.setWorkflowDetails(workflowDetails);
                responseDTO.setBankDetail(bankDetails.stream()
                                .map(claimApplicationBankResponseMapper::toResponse)
                                .findFirst()
                                .orElse(null));
                return ApiResponseDTO.success(responseDTO);
        }

        @Override
        public ApiResponseDTO<GeneralSpecialCaseResponse> approveSpecialCase(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request) {
                                System.out.println("========== START: approveSpecialCase ==========");
                // 1. Get and update claim application
                ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
                claimApplication.setStatus(getStatusById(6L));
                claimApplicationRepository.save(claimApplication);
                // 2. Approve the claim
                claimApplicationApprovalService.approve(applicationNumber, request).getData();
                ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                .getByApplicationNumber(applicationNumber).getData();

                // 3. Get special case response
                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .getByApplicationNumber(applicationNumber);

                // 4. Get bank details
                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                .getByApplicationNumber(applicationNumber);

                // 5. Get workflow details
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(applicationNumber);

                // 6. Get verification details
                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .getByApplicationNumber(applicationNumber);

                // 7. Build response DTO
                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);
                responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                responseDTO.setVerificationDetail(claimVerifier.getData());
                responseDTO.setWorkflowDetails(workflowDetails);
                responseDTO.setBankDetail(bankDetails.stream()
                                .map(claimApplicationBankResponseMapper::toResponse)
                                .findFirst()
                                .orElse(null));
                responseDTO.setApprovalDetail(getApproval);

                // 8. Create special case
                GeneralSpecialCaseResponse specialCaseResponseDTO = specialCaseService.createSpecialCase(responseDTO);
                specialCaseResponseDTO.setApprovalDetail(getApproval);
                specialCaseResponseDTO.setWorkflowDetails(workflowDetails);
                specialCaseResponseDTO.setVerificationDetail(claimVerifier.getData());

                // ============================================================
                // 9. CREATE LEDGER ENTRIES FOR SPECIAL CASE
                // ============================================================
                try {

                        // Check if special case is Approved
                        if ("Approved".equalsIgnoreCase(specialCaseResponseDTO.getStatusName())) {

                                // Create ledger entries using the special case ledger service
                                AccountingEventResponseDto accountingEvent = specialCaseLedgerService
                                                .createSpecialCaseLedgerEntries(
                                                                specialCaseResponseDTO,
                                                                request.getApprovedBy());

                                // Set accounting event detail in response
                                specialCaseResponseDTO.setAccountingEventDetail(accountingEvent);

                                System.out.println("Ledger entries created successfully for special case: "
                                                + applicationNumber);
                                System.out.println("Accounting Event ID: " + accountingEvent.getId() +
                                                ", Total DR: " + accountingEvent.getTotalDr() +
                                                ", Total CR: " + accountingEvent.getTotalCr());
                        } else {
                                System.out.println("Special case is not Approved. Status: "
                                                + specialCaseResponseDTO.getStatusName()
                                                + ", Skipping ledger creation.");
                        }

                } catch (Exception e) {
                        System.out.println("Failed to create ledger entries for special case: " +
                                        applicationNumber);
                        e.printStackTrace();
                        // You can either:
                        // Option 1: Throw exception and rollback
                        throw new RuntimeException("Failed to create ledger entries: " + e.getMessage());
                        // Option 2: Log error but continue (if ledger failure shouldn't block approval)
                        // log.error("Ledger creation failed but claim is approved. Manual intervention
                        // needed.");
                }

                System.out.println("========== END: approveSpecialCase SUCCESS ==========");
                return ApiResponseDTO.success(specialCaseResponseDTO);
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> rejectSpecialCase(String applicationNumber,
                        String rejectedBy, String rejectedRemarks) {
                ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
                claimApplication.setStatus(getStatusById(63L));
                claimApplicationRepository.save(claimApplication);
                claimApplicationApprovalService
                                .verifiedClaimActionRejectedByApprover(applicationNumber, rejectedBy, rejectedRemarks)
                                .getData();
                ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                .getByApplicationNumber(applicationNumber).getData();
                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .getByApplicationNumber(applicationNumber);
                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                .getByApplicationNumber(applicationNumber);
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(applicationNumber);
                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .getByApplicationNumber(applicationNumber);
                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);
                responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                responseDTO.setVerificationDetail(claimVerifier.getData());
                responseDTO.setWorkflowDetails(workflowDetails);
                responseDTO.setBankDetail(bankDetails.stream()
                                .map(claimApplicationBankResponseMapper::toResponse)
                                .findFirst()
                                .orElse(null));
                responseDTO.setApprovalDetail(getApproval);
                return ApiResponseDTO.success(responseDTO);
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> verifiedClaimActionClaimedBy(
                        String applicationNumber, String claimedBy) {
                ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
                claimApplication.setStatus(getStatusById(61L));
                claimApplicationRepository.save(claimApplication);
                claimApplicationApprovalService.verifiedClaimActionClaimedBy(applicationNumber, claimedBy).getData();
                ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                .verifiedClaimActionClaimedBy(applicationNumber, claimedBy).getData();
                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .getByApplicationNumber(applicationNumber);
                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                .getByApplicationNumber(applicationNumber);
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(applicationNumber);
                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .getByApplicationNumber(applicationNumber);
                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);
                responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                responseDTO.setVerificationDetail(claimVerifier.getData());
                responseDTO.setWorkflowDetails(workflowDetails);
                responseDTO.setBankDetail(bankDetails.stream()
                                .map(claimApplicationBankResponseMapper::toResponse)
                                .findFirst()
                                .orElse(null));
                responseDTO.setApprovalDetail(getApproval);
                return ApiResponseDTO.success(responseDTO);
        }

        @Override
        @Transactional
        public ApiResponseDTO<GeneralSpecialCaseApplicationResponseDTO> verifiedClaimActionUnClaimedBy(
                        String applicationNumber, String unClaimedBy) {
                ClaimApplication claimApplication = claimApplicationService.getByApplicationNumber(applicationNumber);
                claimApplication.setStatus(getStatusById(62L));
                claimApplicationRepository.save(claimApplication);
                claimApplicationApprovalService.verifiedClaimActionUnClaimedBy(applicationNumber, unClaimedBy)
                                .getData();
                ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                .verifiedClaimActionUnClaimedBy(applicationNumber, unClaimedBy).getData();
                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                .getByApplicationNumber(applicationNumber);
                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                .getByApplicationNumber(applicationNumber);
                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                .getByApplicationNumber(applicationNumber);
                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                .getByApplicationNumber(applicationNumber);
                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                .mapToGeneralClaimResponse(claimApplication);
                responseDTO.setClaimSpecialCaseApplicationResponseDto(specialCaseResponse.getData());
                responseDTO.setVerificationDetail(claimVerifier.getData());
                responseDTO.setWorkflowDetails(workflowDetails);
                responseDTO.setBankDetail(bankDetails.stream()
                                .map(claimApplicationBankResponseMapper::toResponse)
                                .findFirst()
                                .orElse(null));
                responseDTO.setApprovalDetail(getApproval);
                return ApiResponseDTO.success(responseDTO);
        }

        @Override
        @Transactional
        public ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getSpecialCaseUserCode(String userCode) {
                List<ClaimApplication> claimApplications = claimApplicationService
                                .getByUserCodeAndSpecialClaim(userCode);

                if (claimApplications == null || claimApplications.isEmpty()) {
                        System.out.println("i am empty");
                        return ApiResponseDTO.success("No special case applications found for user code: " + userCode,
                                        null);
                }

                List<GeneralSpecialCaseApplicationResponseDTO> response = claimApplications.stream()
                                .map(claimApplication -> {
                                        try {

                                                // Get special case details
                                                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                if (specialCaseResponse == null
                                                                || specialCaseResponse.getData() == null) {
                                                        System.out.println("Skipping claim - No special case found: "
                                                                        + claimApplication.getApplicationNumber());
                                                        return null;
                                                }

                                                // Get approval details
                                                ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                                                .getByApplicationNumber(
                                                                                claimApplication.getApplicationNumber())
                                                                .getData();

                                                // Get bank details
                                                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Get workflow details
                                                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Get verification details
                                                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Build response
                                                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                                                .mapToGeneralClaimResponse(claimApplication);

                                                responseDTO.setClaimSpecialCaseApplicationResponseDto(
                                                                specialCaseResponse.getData());
                                                responseDTO.setVerificationDetail(
                                                                claimVerifier != null ? claimVerifier.getData() : null);
                                                responseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails
                                                                : new ArrayList<>());

                                                if (bankDetails != null && !bankDetails.isEmpty()) {
                                                        responseDTO.setBankDetail(claimApplicationBankResponseMapper
                                                                        .toResponse(bankDetails.get(0)));
                                                } else {
                                                        responseDTO.setBankDetail(null);
                                                }

                                                responseDTO.setApprovalDetail(getApproval);

                                                return responseDTO;

                                        } catch (Exception e) {
                                                System.err.println("Error processing claim: "
                                                                + claimApplication.getApplicationNumber());
                                                e.printStackTrace();
                                                return null;
                                        }
                                })
                                .filter(Objects::nonNull) // ✅ FILTER OUT NULL VALUES HERE
                                .collect(Collectors.toList());

                if (response.isEmpty()) {
                        return ApiResponseDTO.success("No special case applications found for user code: " + userCode,
                                        response);
                }

                return ApiResponseDTO.success(response);
        }

        private StatusMaster getStatusById(Long statusId) {
                return statusRepository.findById(statusId)
                                .orElseThrow(() -> ClaimException.notFound("Status not found with id: " + statusId));
        }

        public ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getAllSpecialCase() {
                List<ClaimApplication> claimApplications = claimApplicationService.getAllSpecialCase();

                if (claimApplications == null || claimApplications.isEmpty()) {
                        System.out.println("i am empty");
                        return ApiResponseDTO.success("No special case applications found.", null);
                }

                List<GeneralSpecialCaseApplicationResponseDTO> response = claimApplications.stream()
                                .map(claimApplication -> {
                                        try {

                                                // Get special case details
                                                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                if (specialCaseResponse == null
                                                                || specialCaseResponse.getData() == null) {
                                                        System.out.println("Skipping claim - No special case found: "
                                                                        + claimApplication.getApplicationNumber());
                                                        return null;
                                                }

                                                // Get approval details
                                                ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                                                .getByApplicationNumber(
                                                                                claimApplication.getApplicationNumber())
                                                                .getData();

                                                // Get bank details
                                                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Get workflow details
                                                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Get verification details
                                                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Build response
                                                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                                                .mapToGeneralClaimResponse(claimApplication);

                                                responseDTO.setClaimSpecialCaseApplicationResponseDto(
                                                                specialCaseResponse.getData());
                                                responseDTO.setVerificationDetail(
                                                                claimVerifier != null ? claimVerifier.getData() : null);
                                                responseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails
                                                                : new ArrayList<>());

                                                if (bankDetails != null && !bankDetails.isEmpty()) {
                                                        responseDTO.setBankDetail(claimApplicationBankResponseMapper
                                                                        .toResponse(bankDetails.get(0)));
                                                } else {
                                                        responseDTO.setBankDetail(null);
                                                }

                                                responseDTO.setApprovalDetail(getApproval);

                                                return responseDTO;

                                        } catch (Exception e) {
                                                System.err.println("Error processing claim: "
                                                                + claimApplication.getApplicationNumber());
                                                e.printStackTrace();
                                                return null;
                                        }
                                })
                                .filter(Objects::nonNull) // ✅ FILTER OUT NULL VALUES HERE
                                .collect(Collectors.toList());

                if (response.isEmpty()) {
                        return ApiResponseDTO.success("No special case applications found.", response);
                }

                return ApiResponseDTO.success(response);
        }

        public ApiResponseDTO<List<GeneralSpecialCaseApplicationResponseDTO>> getAllSpecialCaseWithClaimedBy(
                        String claimedBy) {

                List<ClaimApplication> claimApplications = claimApplicationService
                                .getAllSpecialCaseWithClaimedBy(claimedBy);

                if (claimApplications == null || claimApplications.isEmpty()) {
                        System.out.println("i am empty");
                        return ApiResponseDTO.success("No special case applications found.", null);
                }

                List<GeneralSpecialCaseApplicationResponseDTO> response = claimApplications.stream()
                                .map(claimApplication -> {
                                        try {

                                                // Get special case details
                                                ApiResponseDTO<ClaimSpecialCaseApplicationResponseDto> specialCaseResponse = claimSpecialCaseApplicationService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                if (specialCaseResponse == null
                                                                || specialCaseResponse.getData() == null) {
                                                        System.out.println("Skipping claim - No special case found: "
                                                                        + claimApplication.getApplicationNumber());
                                                        return null;
                                                }

                                                // Get approval details
                                                ClaimApplicationApprovalResponseDto getApproval = claimApplicationApprovalService
                                                                .getByApplicationNumber(
                                                                                claimApplication.getApplicationNumber())
                                                                .getData();

                                                // Get bank details
                                                List<ClaimApplicationBankDetail> bankDetails = claimApplicationBankDetailService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Get workflow details
                                                List<ClaimApplicationWorkflowResponseDto> workflowDetails = claimApplicationWorkflowService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Get verification details
                                                ApiResponseDTO<ClaimApplicationVerificationResponseDto> claimVerifier = claimApplicationVerificationService
                                                                .getByApplicationNumber(claimApplication
                                                                                .getApplicationNumber());

                                                // Build response
                                                GeneralSpecialCaseApplicationResponseDTO responseDTO = specialCaseGeneralResponseMapper
                                                                .mapToGeneralClaimResponse(claimApplication);

                                                responseDTO.setClaimSpecialCaseApplicationResponseDto(
                                                                specialCaseResponse.getData());
                                                responseDTO.setVerificationDetail(
                                                                claimVerifier != null ? claimVerifier.getData() : null);
                                                responseDTO.setWorkflowDetails(workflowDetails != null ? workflowDetails
                                                                : new ArrayList<>());

                                                if (bankDetails != null && !bankDetails.isEmpty()) {
                                                        responseDTO.setBankDetail(claimApplicationBankResponseMapper
                                                                        .toResponse(bankDetails.get(0)));
                                                } else {
                                                        responseDTO.setBankDetail(null);
                                                }

                                                responseDTO.setApprovalDetail(getApproval);

                                                return responseDTO;

                                        } catch (Exception e) {
                                                System.err.println("Error processing claim: "
                                                                + claimApplication.getApplicationNumber());
                                                e.printStackTrace();
                                                return null;
                                        }
                                })
                                .filter(Objects::nonNull) // ✅ FILTER OUT NULL VALUES HERE
                                .collect(Collectors.toList());

                if (response.isEmpty()) {
                        return ApiResponseDTO.success("No special case applications found.", response);
                }

                return ApiResponseDTO.success(response);
        }

}
