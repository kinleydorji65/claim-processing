package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationDeductionRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.GeneralClaimApplicationVerifierRequestDTO;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;
import com.claim.claim_processing.application.mapper.application.GeneralClaimResponseBuilderMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.BeneficiarySettlementResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationBankResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationCalculationSummaryResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationDeductionResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.ClaimApplicationForfeitedComponentResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.LegalRecoveryResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.NormalClaimResponseMapper;
import com.claim.claim_processing.application.mapper.claimApplicationOtherResponse.PartialWithdrawalResponseMapper;
import com.claim.claim_processing.application.mapper.workFlow.ClaimApplicationVerificationMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.workFlow.ClaimApplicationVerificationRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationCalculationService;
import com.claim.claim_processing.application.service.application.ClaimApplicationDeductionDetailService;
import com.claim.claim_processing.application.service.application.ClaimApplicationForfeitedComponentService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationApprovalService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimApplicationVerificationServiceImpl
                implements ClaimApplicationVerificationService {

        private final ClaimApplicationVerificationRepository verificationRepository;
        private final ClaimApplicationRepository claimApplicationRepository;

        private final StatusMasterRepository statusRepository;
        private final ClaimApplicationWorkflowService workflowService;

        private final ClaimApplicationVerificationMapper verificationMapper;
        private final ClaimApplicationCalculationService calculationService;

        private final GeneralClaimResponseBuilderMapper generalClaimResponseBuilderMapper;
        private final BeneficiarySettlementResponseMapper beneficiarySettlementResponseMapper;
        private final ClaimApplicationBankResponseMapper claimApplicationBankResponseMapper;
        private final ClaimApplicationCalculationSummaryResponseMapper claimApplicationCalculationSummaryResponseMapper;
        private final ClaimApplicationDeductionResponseMapper claimApplicationDeductionResponseMapper;
        private final ClaimApplicationForfeitedComponentResponseMapper claimApplicationForfeitedComponentResponseMapper;
        private final NormalClaimResponseMapper normalClaimResponseMapper;
        private final PartialWithdrawalResponseMapper partialWithdrawalResponseMapper;
        private final LegalRecoveryResponseMapper legalRecoveryResponseMapper;
        private final ClaimApplicationWorkflowService claimWorkFlowService;

        private final ClaimApplicationApprovalService claimApplicationApprovalService;
        private final ClaimApplicationDeductionDetailService claimApplicationDeductionDetailService;
        private final ClaimApplicationForfeitedComponentService claimApplicationForfeitedComponentService;

        @Override
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> patch(
                        String applicationNumber,
                        ClaimApplicationVerificationRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                ClaimApplicationVerification verification = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElseGet(() -> ClaimApplicationVerification.builder()
                                                .claimApplication(claimApplication)
                                                .createdBy(request.getVerifiedBy() != null ? request.getVerifiedBy()
                                                                : "sys")
                                                .build());

                applyRequest(verification);

                verification.setUpdatedBy(request.getVerifiedBy() != null ? request.getVerifiedBy() : "sys");

                ClaimApplicationVerification saved = verificationRepository.save(verification);

                return ApiResponseDTO.success(verificationMapper.toResponse(saved));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getVerifiedClaim() {

                List<ClaimApplicationVerification> verifications = verificationRepository
                                .findByStatus_StatusIdNotIn(List.of(42L, 21L, 1L, 2L, 4L, 5L, 7L, 8L, 61L, 63L));
                if (verifications == null || verifications.isEmpty()) {
                        return ApiResponseDTO.success(null);
                }
                List<ClaimApplicationVerificationResponseDto> responses = verifications.stream()
                                .map(verificationMapper::toResponse)
                                .toList();
                return ApiResponseDTO.success(responses);
        }

        @Override
@Transactional
public ApiResponseDTO<GeneralClaimResponse> verify(
        String applicationNumber,
        GeneralClaimApplicationVerifierRequestDTO request) {
        
    ClaimApplicationVerificationRequestDto verificationDto = request.getVerifierRequest();
    ClaimApplication claimApplication = getClaimApplication(applicationNumber);
    claimApplication.setUpdatedBy(verificationDto.getVerifiedBy());
    claimApplication.setStatus(statusRepository.findById(41L)
            .orElseThrow(() -> ClaimException.notFound("Status not found with id: " + 41L)));
    claimApplicationRepository.save(claimApplication);

    // 1. Save Verification
    ClaimApplicationVerification verification = verificationRepository
            .findByClaimApplication_ApplicationNumber(applicationNumber)
            .orElse(new ClaimApplicationVerification());

    applyRequest(verification);

    if (verificationDto.getVerifiedBy() == null || verificationDto.getVerifiedBy().isBlank()) {
        throw ClaimException.badRequest("Verified By is required");
    }
    verification.setClaimApplication(claimApplication);
    verification.setRejectedBy(verificationDto.getVerifiedBy());
    verification.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
    verification.setUpdatedBy(verificationDto.getVerifiedBy());

    ClaimApplicationVerification saved = verificationRepository.save(verification);

    // 2. Save Workflow
    String reason = verificationDto.getRemarks() != null ? verificationDto.getRemarks()
            : verificationDto.getRemarks();
    Map<String, Long> workflowStage = resolveFromStageAndToStageAndAction(claimApplication.getId(),
            41L);
    ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
            .fromStageId(workflowStage.get("fromStage"))
            .toStageId(workflowStage.get("toStage"))
            .fromStatusId(workflowStage.get("fromStatus"))
            .toStatusId(workflowStage.get("toStatus"))
            .actionId(2L) // Assuming 2L is the action ID for verification
            .reason(reason)
            .actionBy(verificationDto.getVerifiedBy())
            .build();

    workflowService.create(claimApplication, workflowRequest);

    // 3. Save Calculation Summary (ELIGIBLE components)
    if (request.getCalculationSummary() != null) {
        calculationService.createForCalculation(claimApplication, request.getCalculationSummary());
    }

    // ✅ 4. Save DEDUCTION DETAIL (LOAN, RENTAL, TAX, etc.)
    if (request.getCalculationSummary() != null && 
        request.getCalculationSummary().getDeductionDetail() != null) {
        
        // Set the claim application and createdBy if not set
        ClaimApplicationDeductionRequestDto deductionRequest = request.getCalculationSummary().getDeductionDetail();
        
        // If createdBy is null, use the verifiedBy
        if (deductionRequest.getCreatedBy() == null) {
            deductionRequest.setCreatedBy(verificationDto.getVerifiedBy());
        }
        
        // Save deduction details
        claimApplicationDeductionDetailService.saveCalculationDeductions(claimApplication, deductionRequest);
    }

    // ✅ 5. Save FORFEITED COMPONENTS (if any)
    if (request.getCalculationSummary() != null && 
        request.getCalculationSummary().getForFeitedComponents() != null && 
        !request.getCalculationSummary().getForFeitedComponents().isEmpty()) {
        
        // You need to implement this service
        claimApplicationForfeitedComponentService.saveForfeitedComponents(
            claimApplication, 
            request.getCalculationSummary().getForFeitedComponents()
        );
    }

    ClaimApplicationVerification claimVerification = verificationRepository
            .findByClaimApplication_ApplicationNumber(claimApplication.getApplicationNumber())
            .orElse(null);
    
    GeneralClaimResponse response = buildGeneralClaimResponse(claimApplication, claimVerification);
    return ApiResponseDTO.success(response);
}

        private GeneralClaimResponse buildGeneralClaimResponse(
                        ClaimApplication claimApplication, ClaimApplicationVerification claimVerification) {

                if (claimApplication == null) {
                        return null;
                }

                GeneralClaimResponse response = generalClaimResponseBuilderMapper.toResponse(claimApplication);

                response.setNormalClaimDetails(
                                normalClaimResponseMapper.toResponse(
                                                claimApplication.getNormalClaimDetail() != null
                                                                ? claimApplication.getNormalClaimDetail()
                                                                : null));

                response.setPartialWithdrawalDetails(
                                partialWithdrawalResponseMapper.toResponse(
                                                claimApplication.getPartialWithdrawalDetail() != null
                                                                ? claimApplication.getPartialWithdrawalDetail()
                                                                : null));

                response.setBeneficiarySettlementDetails(
                                beneficiarySettlementResponseMapper.toResponse(
                                                claimApplication.getBeneficiarySettlementDetail() != null
                                                                ? claimApplication.getBeneficiarySettlementDetail()
                                                                : null));

                response.setBankDetails(
                                claimApplication.getBankDetails() == null
                                                ? List.of()
                                                : claimApplication.getBankDetails()
                                                                .stream()
                                                                .map(claimApplicationBankResponseMapper::toResponse)
                                                                .toList());

                response.setDeductionDetail(
                                claimApplication.getDeductionDetail() == null
                                                ? null
                                                : claimApplicationDeductionResponseMapper.toResponse(
                                                                claimApplication.getDeductionDetail()));

                response.setCalculationSummary(
                                claimApplication.getCalculationSummary() == null
                                                ? null
                                                : claimApplicationCalculationSummaryResponseMapper.toResponse(
                                                                claimApplication.getCalculationSummary()));

                response.setForfeitedComponents(
                                claimApplication.getForfeitedComponents() == null
                                                ? List.of()
                                                : claimApplication.getForfeitedComponents()
                                                                .stream()
                                                                .map(claimApplicationForfeitedComponentResponseMapper::toResponse)
                                                                .toList());
                response.setWorkflowDetails(
                                claimWorkFlowService.getByApplicationId(claimApplication.getId()) != null
                                                ? claimWorkFlowService
                                                                .getByApplicationId(claimApplication.getId())
                                                : List.of());

                response.setVerificationDetail(
                                claimVerification != null ? verificationMapper.toResponse(claimVerification) : null);

                response.setApprovalDetail(
                                claimApplicationApprovalService
                                                .getByApplicationNumber(claimApplication.getApplicationNumber()) != null
                                                                ? (claimApplicationApprovalService
                                                                                .getByApplicationNumber(claimApplication
                                                                                                .getApplicationNumber()) != null)
                                                                                                                ? claimApplicationApprovalService
                                                                                                                                .getByApplicationNumber(
                                                                                                                                                claimApplication.getApplicationNumber())
                                                                                                                                .getData()
                                                                                                                : null
                                                                : null);
                response.setLegalRecoveryDetail(
                                claimApplication.getLegalRecoveryDetail() != null
                                                ? legalRecoveryResponseMapper.toResponse(
                                                                claimApplication.getLegalRecoveryDetail())
                                                : null);
                return response;
        }

        @Override
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> rejectedClaimApplication(
                        String applicationNumber,
                        String rejectedBy, String remarks) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                claimApplication.setUpdatedBy(rejectedBy);
                claimApplication.setStatus(statusRepository.findById(7L)
                                .orElseThrow(() -> ClaimException.notFound("Status not found with id: " + 41L)));
                claimApplicationRepository.save(claimApplication);

                ClaimApplicationVerification rejection = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(new ClaimApplicationVerification());

                applyRequest(rejection);

                if (rejectedBy == null || rejectedBy.isBlank()) {
                        throw ClaimException.badRequest("Rejected By is required");
                }
                rejection.setClaimApplication(claimApplication);
                rejection.setRemarks(remarks);
                rejection.setRejectedBy(rejectedBy);
                rejection.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
                rejection.setUpdatedBy(rejectedBy);

                ClaimApplicationVerification saved = verificationRepository.save(rejection);

                String reason = remarks != null ? remarks
                                : remarks;
                Map<String, Long> workflowStage = resolveFromStageAndToStageAndAction(claimApplication.getId(),
                                7L);
                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                                .fromStageId(workflowStage.get("fromStage"))
                                .toStageId(workflowStage.get("toStage"))
                                .fromStatusId(workflowStage.get("fromStatus"))
                                .toStatusId(workflowStage.get("toStatus"))
                                .actionId(21L)
                                .reason(reason)
                                .actionBy(rejectedBy)
                                .build();

                workflowService.create(claimApplication, workflowRequest);

                return ApiResponseDTO.success(verificationMapper.toResponse(saved));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> getByVerifiedApplicationNumber(
                        String applicationNumber) {

                ClaimApplicationVerification verification = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(null);
                if (verification == null) {
                        return ApiResponseDTO.success(
                                        "Claim application not found for application number: " + applicationNumber,
                                        null);
                }
                return ApiResponseDTO.success(verificationMapper.toResponse(verification));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getVerifiedClaimButRejectedClaim() {

                List<ClaimApplicationVerification> verifications = verificationRepository
                                .findByStatus_StatusId(7L);
                if (verifications.isEmpty()) {
                        return ApiResponseDTO.success("No verified but rejected claims found", null);
                }

                List<ClaimApplicationVerificationResponseDto> responses = verifications.stream()
                                .map(verificationMapper::toResponse)
                                .toList();
                return ApiResponseDTO.success(responses);
        }

        private void applyRequest(
                        ClaimApplicationVerification verification) {

                verification.setStatus(
                                statusRepository.findById(41L)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Verification status not found with id: "
                                                                                + 41L)));
        }

        private Map<String, Long> resolveFromStageAndToStageAndAction(
                        Long applicationId,
                        Long statusId) {
                ClaimApplicationWorkflowResponseDto stageMaster = workflowService.getByApplicationId(applicationId)
                                .get(0);
                if (isVerified(statusId)) {
                        return Map.of(
                                        "fromStage", 3L,
                                        "toStage", 4L,
                                        "fromStatus", 2L,
                                        "toStatus", 41L);
                }

                return Map.of(
                                "fromStage", 4L,
                                "toStage", stageMaster.getFromStageId(),
                                "fromStatus", stageMaster.getToStatusId(),
                                "toStatus", 7L);

        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getClaimApplicationWhichClaimedBy(
                        String claimedBy) {
                List<ClaimApplicationVerification> verifications = null;
                verifications = verificationRepository
                                .findByClaimedByAndStatus_StatusId(claimedBy, 3L);
                if (verifications == null || verifications.isEmpty()) {
                        return ApiResponseDTO.success("Claim application not found for claimed by: " + claimedBy, null);
                }
                return ApiResponseDTO.success(verifications.stream()
                                .map(verificationMapper::toResponse)
                                .toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> verifiedClaimApplicationClaimedBy(
                        String applicationNumber, String claimedBy) {
                if (applicationNumber == null || applicationNumber.isBlank()) {
                        throw ClaimException.badRequest("Application number is required");
                }
                if (claimedBy == null || claimedBy.isBlank()) {
                        throw ClaimException.badRequest("Claimed by is required");
                }

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                ClaimApplicationVerification verification = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(new ClaimApplicationVerification());
                StatusMaster claimedStatus = statusRepository.findById(3L)
                                .orElseThrow(() -> new RuntimeException("Claimed status not found"));
                verification.setStatus(claimedStatus);
                verification.setClaimApplication(claimApplication);
                verification.setClaimedBy(claimedBy);
                verification.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                verificationRepository.saveAndFlush(verification);
                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService
                                .getByApplicationNumber(applicationNumber);
                if (workflowResponse == null || workflowResponse.isEmpty()) {
                        ClaimApplicationWorkflowResponseDto workFlow = workflowResponse.get(0);
                        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                        .builder()
                                        .fromStageId(workFlow.getFromStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .fromStatusId(workFlow.getFromStatusId())
                                        .toStatusId(3L)
                                        .reason(workFlow.getReason())
                                        .actionBy(workFlow.getActionBy())
                                        .build();
                        workflowService
                                        .create(claimApplication, workflowRequest);
                }
                List<ClaimApplicationVerification> getVerifications = verificationRepository
                                .findByClaimedByAndStatus_StatusId(claimedBy, 3L);
                return ApiResponseDTO.success(getVerifications.stream()
                                .map(verificationMapper::toResponse)
                                .toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> verifiedClaimApplicationUnClaimedBy(
                        String applicationNumber, String unClaimedBy) {
                if (applicationNumber == null || applicationNumber.isBlank()) {
                        throw ClaimException.badRequest("Application number is required");
                }
                if (unClaimedBy == null || unClaimedBy.isBlank()) {
                        throw ClaimException.badRequest("Unclaimed by is required");
                }
                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                ClaimApplicationVerification verification = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(new ClaimApplicationVerification());
                verification.setClaimApplication(claimApplication);
                StatusMaster unClaimedStatus = statusRepository.findById(3L)
                                .orElseThrow(() -> new RuntimeException("Unclaimed status not found"));
                claimApplication.setStatus(unClaimedStatus);
                claimApplication.setUnClaimedBy(null);
                verification.setStatus(unClaimedStatus);
                verification.setClaimedBy(null);
                claimApplicationRepository.saveAndFlush(claimApplication);
                verification.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                verificationRepository.saveAndFlush(verification);
                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService
                                .getByApplicationNumber(applicationNumber);
                if (workflowResponse == null || workflowResponse.isEmpty()) {
                        ClaimApplicationWorkflowResponseDto workFlow = workflowResponse.get(0);
                        ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto
                                        .builder()
                                        .fromStageId(workFlow.getFromStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .toStageId(workFlow.getToStageId())
                                        .fromStatusId(workFlow.getFromStatusId())
                                        .toStatusId(4L)
                                        .reason(workFlow.getReason())
                                        .actionBy(workFlow.getActionBy())
                                        .build();
                        workflowService
                                        .create(claimApplication, workflowRequest);
                }
                List<ClaimApplicationVerification> getVerifications = verificationRepository
                                .findByClaimedByAndStatus_StatusId(unClaimedBy, 4L);

                return ApiResponseDTO.success(getVerifications.stream()
                                .map(verificationMapper::toResponse)
                                .toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getVerifiedApplication() {
                List<ClaimApplicationVerification> verifications = verificationRepository
                                .findByStatus_StatusId(41L);
                return ApiResponseDTO.success(verifications.stream()
                                .map(verificationMapper::toResponse)
                                .toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> getByApplicationNumber(
                        String applicationNumber) {
                ClaimApplicationVerification verifications = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(null);
                return ApiResponseDTO.success(verificationMapper.toResponse(verifications));
        }

        private boolean isVerified(Long statusId) {

                String name = getReviewStatusName(statusId);

                return "VERIFIED".equalsIgnoreCase(name.toUpperCase());
        }

        private String getReviewStatusName(Long id) {
                StatusMaster verificationStatus = statusRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Verification status not found with id: " + id));
                return verificationStatus.getStatusName();
        }

        private ClaimApplication getClaimApplication(String applicationNumber) {
                return claimApplicationRepository.findByApplicationNumber(applicationNumber)
                                .orElseThrow(() -> new RuntimeException(
                                                "Claim application not found with application number: "
                                                                + applicationNumber));
        }
}