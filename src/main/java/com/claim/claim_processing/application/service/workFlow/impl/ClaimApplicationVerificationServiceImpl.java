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
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.VerificationStatusMaster;
import com.claim.claim_processing.common.repository.common.ReviewStatusRepository;
import com.claim.claim_processing.common.repository.statusMaster.VerificationStatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
public class ClaimApplicationVerificationServiceImpl
        implements ClaimApplicationVerificationService {

    private final ClaimApplicationVerificationRepository verificationRepository;
    private final ClaimApplicationRepository claimApplicationRepository;

    private final VerificationStatusMasterRepository verificationStatusRepository;
    private final ReviewStatusRepository reviewStatusRepository;
    private final ClaimApplicationWorkflowService workflowService;

    private final ClaimApplicationVerificationMapper verificationMapper;

    @Override
    public ClaimApplicationVerificationResponseDto patch(
            Long claimApplicationId,
            ClaimApplicationVerificationRequestDto request
    ) {

        ClaimApplication claimApplication = getClaimApplication(claimApplicationId);

        ClaimApplicationVerification verification =
                verificationRepository.findByClaimApplication_Id(claimApplicationId)
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

        return verificationMapper.toResponse(saved);
    }

    @Override
public ClaimApplicationVerificationResponseDto verify(
        Long claimApplicationId,
        ClaimApplicationVerificationRequestDto request
) {

    ClaimApplication claimApplication = getClaimApplication(claimApplicationId);

    ClaimApplicationVerification verification =
            verificationRepository.findByClaimApplication_Id(claimApplicationId)
                    .orElseThrow(() -> ClaimException.notFound(
                            "Verification record not found for claim application id: " + claimApplicationId
                    ));

    applyRequest(verification, request);

    if (request.getVerifiedBy() == null || request.getVerifiedBy().isBlank()) {
        throw ClaimException.badRequest("Verified By is required");
    }

    verification.setVerifiedBy(request.getVerifiedBy());
    verification.setVerifiedByRole(request.getVerifiedByRole());
    verification.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
    verification.setUpdatedBy(request.getVerifiedBy());

    ClaimApplicationVerification saved = verificationRepository.save(verification);

    String reason = request.getReturnReason() != null ? request.getReturnReason() :
            request.getRejectionReason() != null ? request.getRejectionReason() :
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

    return verificationMapper.toResponse(saved);
}

    @Override
    @Transactional(readOnly = true)
    public ClaimApplicationVerificationResponseDto getByClaimApplicationId(
            Long claimApplicationId
    ) {

        ClaimApplicationVerification verification =
                verificationRepository.findByClaimApplication_Id(claimApplicationId)
                        .orElse(null);
        if (verification == null) {
            return null;
        }
        return verificationMapper.toResponse(verification);
    }

    private void applyRequest(
            ClaimApplicationVerification verification,
            ClaimApplicationVerificationRequestDto request
    ) {

        if (request.getVerificationStatusId() != null) {
            verification.setVerificationStatus(
                    verificationStatusRepository.findById(request.getVerificationStatusId())
                            .orElseThrow(() -> new RuntimeException("Verification status not found with id: " + request.getVerificationStatusId()))
            );
        }

        if (request.getMemberReviewStatusId() != null) {
            verification.setMemberReviewStatus(
                    reviewStatusRepository.findById(request.getMemberReviewStatusId())
                            .orElseThrow(() -> new RuntimeException("Member review status not found with id: " + request.getMemberReviewStatusId()))
            );
        }

        if (request.getBankReviewStatusId() != null) {
            verification.setBankReviewStatus(
                    reviewStatusRepository.findById(request.getBankReviewStatusId())
                            .orElseThrow(() -> new RuntimeException("Bank review status not found with id: " + request.getBankReviewStatusId()))
            );
        }

        if (request.getDocumentReviewStatusId() != null) {
            verification.setDocumentReviewStatus(
                    reviewStatusRepository.findById(request.getDocumentReviewStatusId())
                            .orElseThrow(() -> new RuntimeException("Document review status not found with id: " + request.getDocumentReviewStatusId()))
            );
        }

        if (request.getContributionReviewStatusId() != null) {
            verification.setContributionReviewStatus(
                    reviewStatusRepository.findById(request.getContributionReviewStatusId())
                            .orElseThrow(() -> new RuntimeException("Contribution review status not found with id: " + request.getContributionReviewStatusId()))
            );
        }

        if (request.getRuleReviewStatusId() != null) {
            verification.setRuleReviewStatus(
                    reviewStatusRepository.findById(request.getRuleReviewStatusId())
                            .orElseThrow(() -> new RuntimeException("Rule review status not found with id: " + request.getRuleReviewStatusId()))
            );
        }

        if (request.getDeductionReviewStatusId() != null) {
            verification.setDeductionReviewStatus(
                    reviewStatusRepository.findById(request.getDeductionReviewStatusId())
                            .orElseThrow(() -> new RuntimeException("Deduction review status not found with id: " + request.getDeductionReviewStatusId()))
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

        verification.setReturnReason(request.getReturnReason());
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

private boolean isVerifiedOrApproved(Long statusId) {

    String code = getReviewStatusCode(statusId);

    return "VERIFIED".equalsIgnoreCase(code)
            || "APPROVED".equalsIgnoreCase(code);
}

    private String getReviewStatusCode(Long id) {
        VerificationStatusMaster verificationStatus = verificationStatusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Verification status not found with id: " + id));
        return verificationStatus.getCode();
    }

    private ClaimApplication getClaimApplication(Long claimApplicationId) {
        return claimApplicationRepository.findById(claimApplicationId)
                .orElseThrow(() -> new RuntimeException(
                        "Claim application not found with id: " + claimApplicationId
                ));
    }
}