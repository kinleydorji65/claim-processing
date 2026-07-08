package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;
import com.claim.claim_processing.application.mapper.workFlow.ClaimApplicationVerificationMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.workFlow.ClaimApplicationVerificationRepository;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationWorkflowService;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
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

        @Override
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> patch(
                        String applicationNumber,
                        ClaimApplicationVerificationRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                ClaimApplicationVerification verification = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElseGet(() -> ClaimApplicationVerification.builder()
                                                .claimApplication(claimApplication)
                                                .isActive(ActivityEnum.Y)
                                                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy()
                                                                : "sys")
                                                .build());

                applyRequest(verification, request);

                verification.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "sys");

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
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> verify(
                        String applicationNumber,
                        ClaimApplicationVerificationRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);
                claimApplication.setUpdatedBy(request.getVerifiedBy());
                claimApplication.setStatus(statusRepository.findById(41L)
                                .orElseThrow(() -> ClaimException.notFound("Status not found with id: " + 41L)));
                claimApplicationRepository.save(claimApplication);

                ClaimApplicationVerification verification = verificationRepository
                                .findByClaimApplication_ApplicationNumber(applicationNumber)
                                .orElse(new ClaimApplicationVerification());

                applyRequest(verification, request);

                if (request.getVerifiedBy() == null || request.getVerifiedBy().isBlank()) {
                        throw ClaimException.badRequest("Verified By is required");
                }
                verification.setClaimApplication(claimApplication);
                verification.setRejectedBy(request.getVerifiedBy());
                verification.setVerifiedByRoleId(request.getVerifiedByRoleId());
                verification.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
                verification.setUpdatedBy(request.getVerifiedBy());

                ClaimApplicationVerification saved = verificationRepository.save(verification);

                String reason = request.getRejectionReason() != null ? request.getRejectionReason()
                                : request.getVerifierRemarks();
                Map<String, Long> workflowStage = resolveFromStageAndToStageAndAction(claimApplication.getId(),
                                request);
                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                                .fromStageId(workflowStage.get("fromStage"))
                                .toStageId(workflowStage.get("toStage"))
                                .fromStatusId(workflowStage.get("fromStatus"))
                                .toStatusId(workflowStage.get("toStatus"))
                                .actionId(2L) // Assuming 2L is the action ID for verification
                                .reason(reason)
                                .actionBy(request.getVerifiedBy())
                                .build();

                workflowService.create(claimApplication, workflowRequest);

                return ApiResponseDTO.success(verificationMapper.toResponse(saved));
        }

        @Override
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> rejectedClaimApplication(
                        String applicationNumber,
                        ClaimApplicationVerificationRequestDto request) {

                ClaimApplication claimApplication = getClaimApplication(applicationNumber);

                claimApplication.setUpdatedBy(request.getVerifiedBy());
                claimApplication.setStatus(statusRepository.findById(7L)
                                .orElseThrow(() -> ClaimException.notFound("Status not found with id: " + 41L)));
                claimApplicationRepository.save(claimApplication);

                ClaimApplicationVerification rejection = verificationRepository
                        .findByClaimApplication_ApplicationNumber(applicationNumber)
                        .orElse(new ClaimApplicationVerification());

                applyRequest(rejection, request);

                if (request.getRejectedBy() == null || request.getRejectedBy().isBlank()) {
                        throw ClaimException.badRequest("Rejected By is required");
                }
                rejection.setClaimApplication(claimApplication);
                rejection.setRejectedBy(request.getRejectedBy());
                rejection.setVerifiedByRoleId(request.getVerifiedByRoleId());
                rejection.setVerifiedByRoleId(26L);
                rejection.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
                rejection.setUpdatedBy(request.getRejectedBy());

                ClaimApplicationVerification saved = verificationRepository.save(rejection);

                String reason = request.getRejectionReason() != null ? request.getRejectionReason()
                                : request.getVerifierRemarks();
                Map<String, Long> workflowStage = resolveFromStageAndToStageAndAction(claimApplication.getId(),
                                request);
                ClaimApplicationWorkflowRequestDto workflowRequest = ClaimApplicationWorkflowRequestDto.builder()
                                .fromStageId(workflowStage.get("fromStage"))
                                .toStageId(workflowStage.get("toStage"))
                                .fromStatusId(workflowStage.get("fromStatus"))
                                .toStatusId(workflowStage.get("toStatus"))
                                .actionId(21L)
                                .reason(reason)
                                .actionBy(request.getVerifiedBy())
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
                        return ApiResponseDTO.success("Claim application not found for application number: " + applicationNumber, null);
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
                        ClaimApplicationVerification verification,
                        ClaimApplicationVerificationRequestDto request) {

                if (request.getVerificationStatusId() != null || request.getVerificationStatusId() < 0) {
                        verification.setStatus(
                                        statusRepository.findById(request.getVerificationStatusId())
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Verification status not found with id: "
                                                                                        + request.getVerificationStatusId())));
                }

                if (request.getRequiresRecalculation() != null) {
                        verification.setRequiresRecalculation(
                                        request.getRequiresRecalculation());
                }

                if (request.getRequiresManualReview() != null) {
                        verification.setRequiresManualReview(
                                        request.getRequiresManualReview());
                }
                verification.setRejectionReason(request.getRejectionReason());
                verification.setVerifierRemarks(request.getVerifierRemarks());
        }

        private Map<String, Long> resolveFromStageAndToStageAndAction(
                        Long applicationId,
                        ClaimApplicationVerificationRequestDto request) {
                ClaimApplicationWorkflowResponseDto stageMaster = workflowService.getByApplicationId(applicationId)
                                .get(0);
                if (isVerified(request.getVerificationStatusId())) {
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
        public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getClaimApplicationWhichClaimedBy(String claimedBy) {
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
                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService.getByApplicationNumber(applicationNumber);
                if(workflowResponse == null || workflowResponse.isEmpty()){
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
                verification.setStatus(unClaimedStatus);
                verification.setClaimedBy(null);
                verification.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                verificationRepository.saveAndFlush(verification);
                List<ClaimApplicationWorkflowResponseDto> workflowResponse = workflowService.getByApplicationNumber(applicationNumber);
                if(workflowResponse == null || workflowResponse.isEmpty()){
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
        public ApiResponseDTO<ClaimApplicationVerificationResponseDto> getByApplicationNumber(String applicationNumber) {
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