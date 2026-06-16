package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationWorkflowRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
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
            ClaimApplicationVerificationRequestDto request
    ) {

        ClaimApplication claimApplication = getClaimApplication(applicationNumber);

        ClaimApplicationVerification verification =
                verificationRepository.findByClaimApplication_ApplicationNumber(applicationNumber)
                        .orElseGet(() -> ClaimApplicationVerification.builder()
                                .claimApplication(claimApplication)
                                .isActive(ActivityEnum.Y)
                                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "sys")
                                .build()
                        );

        applyRequest(verification, request);

        verification.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "sys");

        ClaimApplicationVerification saved =
                verificationRepository.save(verification);

        return ApiResponseDTO.success(verificationMapper.toResponse(saved));
    }

    @Override
public ApiResponseDTO<ClaimApplicationVerificationResponseDto> verify(
        String applicationNumber,
        ClaimApplicationVerificationRequestDto request
) {

    ClaimApplication claimApplication = getClaimApplication(applicationNumber);

    ClaimApplicationVerification verification =
            verificationRepository.findByClaimApplication_ApplicationNumber(applicationNumber)
                    .orElseThrow(() -> ClaimException.notFound(
                            "Verification record not found for claim application number: " + applicationNumber
                    ));

    applyRequest(verification, request);

    if (request.getVerifiedBy() == null || request.getVerifiedBy().isBlank()) {
        throw ClaimException.badRequest("Verified By is required");
    }

    verification.setVerifiedBy(request.getVerifiedBy());
    verification.setVerifiedByRoleId(request.getVerifiedByRoleId());
    verification.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
    verification.setUpdatedBy(request.getVerifiedBy());

    ClaimApplicationVerification saved = verificationRepository.save(verification);

    String reason = request.getRejectionReason() != null ? request.getRejectionReason() :
                    request.getVerifierRemarks();
    Map<String, Long> workflowStage = resolveFromStageAndToStageAndAction(claimApplication.getId(), request);
    ClaimApplicationWorkflowRequestDto workflowRequest =
            ClaimApplicationWorkflowRequestDto.builder()
                    .fromStageId(workflowStage.get("fromStage"))
                    .toStageId(workflowStage.get("toStage"))
                    .fromStatusId(workflowStage.get("fromStatus"))
                    .toStatusId(workflowStage.get("toStatus"))
                    .actionId(request.getActionId())
                    .reason(reason)
                    .actionBy(request.getVerifiedBy())
                    .build();

    workflowService.create(claimApplication, workflowRequest);

    return ApiResponseDTO.success(verificationMapper.toResponse(saved));
}

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO<ClaimApplicationVerificationResponseDto> getByApplicationNumber(
            String applicationNumber
    ) {

        ClaimApplicationVerification verification =
                verificationRepository.findByClaimApplication_ApplicationNumber(applicationNumber)
                        .orElse(null);
                        if (verification == null) {
                                return null;
                        }
        return ApiResponseDTO.success(verificationMapper.toResponse(verification));
    }

    private void applyRequest(
            ClaimApplicationVerification verification,
            ClaimApplicationVerificationRequestDto request
    ) {

        if (request.getVerificationStatusId() != null || request.getVerificationStatusId() < 0) {
            verification.setStatus(
                    statusRepository.findById(request.getVerificationStatusId())
                            .orElseThrow(() -> new RuntimeException("Verification status not found with id: " + request.getVerificationStatusId()))
            );
        }

        if (request.getRequiresRecalculation() != null) {
            verification.setRequiresRecalculation(
                    request.getRequiresRecalculation()
            );
        }

        if (request.getRequiresManualReview() != null) {
            verification.setRequiresManualReview(
                    request.getRequiresManualReview()
            );
        }
        verification.setRejectionReason(request.getRejectionReason());
        verification.setVerifierRemarks(request.getVerifierRemarks());
    }

    private Map<String, Long> resolveFromStageAndToStageAndAction(
        Long applicationId,
         ClaimApplicationVerificationRequestDto request
) {
    ClaimApplicationWorkflowResponseDto stageMaster = workflowService.getByApplicationId(applicationId).get(0);
   if(isVerifiedOrApproved(request.getVerificationStatusId())
            && isVerifiedOrApproved(request.getMemberReviewStatusId())
            && isVerifiedOrApproved(request.getBankReviewStatusId())
            && isVerifiedOrApproved(request.getDocumentReviewStatusId())
            && isVerifiedOrApproved(request.getContributionReviewStatusId())
            && isVerifiedOrApproved(request.getRuleReviewStatusId())
            && isVerifiedOrApproved(request.getDeductionReviewStatusId())){
        return Map.of(
                "fromStage", 3L,
                "toStage", 4L,
                "fromStatus", 2L,
                "toStatus", 41L
        );
    }

    return Map.of(
                "fromStage", stageMaster.getFromStageId(),
                "toStage", stageMaster.getToStageId(),
                "fromStatus", stageMaster.getFromStatusId(),
                "toStatus", 7L
    );
    
}

@Override
@Transactional(readOnly = true)
public ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> verifiedClaimApplicationClaimedBy(String applicationNumber, String claimedBy) {
        if (applicationNumber == null || applicationNumber.isBlank()) {
                throw ClaimException.badRequest("Application number is required");
        }
        if (claimedBy == null || claimedBy.isBlank()) {
                throw ClaimException.badRequest("Claimed by is required");
        }

        ClaimApplicationVerification verification = verificationRepository.findByClaimApplication_ApplicationNumber(applicationNumber)
                .orElseThrow(() -> ClaimException.notFound("Verification record not found for claim application number: " + applicationNumber));
        StatusMaster claimedStatus = statusRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Claimed status not found"));
        verification.setStatus(claimedStatus);
        verification.setClaimedBy(claimedBy);
        verification.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        verificationRepository.saveAndFlush(verification);
        List<ClaimApplicationVerification> getVerifications = verificationRepository.findByClaimedByAndStatus_StatusId(claimedBy, 3L);
        return ApiResponseDTO.success(getVerifications.stream()
                .map(verificationMapper::toResponse)
                .toList());
}

private boolean isVerifiedOrApproved(Long statusId) {

    String code = getReviewStatusCode(statusId);

    return "VERIFIED".equalsIgnoreCase(code)
            || "APPROVED".equalsIgnoreCase(code);
}

    private String getReviewStatusCode(Long id) {
        StatusMaster verificationStatus = statusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Verification status not found with id: " + id));
        return verificationStatus.getStatusName();
    }

    private ClaimApplication getClaimApplication(String applicationNumber) {
        return claimApplicationRepository.findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Claim application not found with application number: " + applicationNumber
                ));
    }
}