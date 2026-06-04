package com.claim.claim_processing.application.service.workFlow.impl;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationWorkflow;
import com.claim.claim_processing.application.mapper.workFlow.ClaimApplicationVerificationMapper;
import com.claim.claim_processing.application.repository.application.ClaimApplicationRepository;
import com.claim.claim_processing.application.repository.workFlow.ClaimApplicationVerificationRepository;
import com.claim.claim_processing.application.repository.workFlow.ClaimApplicationWorkflowRepository;
import com.claim.claim_processing.application.service.workFlow.ClaimApplicationVerificationService;
import com.claim.claim_processing.common.entities.common.*;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.statusMaster.VerificationStatusMaster;
import com.claim.claim_processing.common.repository.common.ActionMasterRepository;
import com.claim.claim_processing.common.repository.common.DecisionRepository;
import com.claim.claim_processing.common.repository.common.ReviewStatusRepository;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.common.repository.statusMaster.VerificationStatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimApplicationVerificationServiceImpl
        implements ClaimApplicationVerificationService {

    private final ClaimApplicationVerificationRepository verificationRepository;
    private final ClaimApplicationWorkflowRepository workflowRepository;
    private final ClaimApplicationRepository claimApplicationRepository;

    private final VerificationStatusMasterRepository verificationStatusRepository;
    private final DecisionRepository decisionRepository;
    private final ReviewStatusRepository reviewStatusRepository;
    private final StatusMasterRepository statusRepository;
    private final StageRepository stageRepository;
    private final ActionMasterRepository actionRepository;

    private final ClaimApplicationVerificationMapper verificationMapper;

    @Override
    public ClaimApplicationVerificationResponseDto create(
            ClaimApplicationVerificationRequestDto request
    ) {

        validateBasicRequest(request);

        ClaimApplication claimApplication = getClaimApplication(request.getClaimApplicationId());

        validateClaimCanBeVerified(claimApplication);

        StatusMaster fromStatus = claimApplication.getStatus();

        ClaimApplicationVerification entity = buildEntity(request, claimApplication);

        VerificationOutcome outcome = applyVerificationDecision(entity);

        updateClaimApplicationStatusAndStage(claimApplication, outcome);

        ClaimApplicationVerification savedVerification = verificationRepository.save(entity);

        claimApplicationRepository.save(claimApplication);

        createWorkflowHistory(
                claimApplication,
                savedVerification,
                fromStatus,
                claimApplication.getStatus(),
                outcome
        );

        return verificationMapper.toResponse(savedVerification);
    }

    @Override
    public ClaimApplicationVerificationResponseDto update(
            Long id,
            ClaimApplicationVerificationRequestDto request
    ) {

        if (id == null) {
            throw ClaimException.badRequest("Verification id is required.");
        }

        ClaimApplicationVerification entity = verificationRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application verification not found with id: " + id
                ));

        ClaimApplication claimApplication = entity.getClaimApplication();

        if (claimApplication == null) {
            throw ClaimException.badRequest("Verification record has no claim application.");
        }

        validateClaimCanBeVerified(claimApplication);

        StatusMaster fromStatus = claimApplication.getStatus();

        updateEntity(entity, request);

        VerificationOutcome outcome = applyVerificationDecision(entity);

        updateClaimApplicationStatusAndStage(claimApplication, outcome);

        ClaimApplicationVerification savedVerification = verificationRepository.save(entity);

        claimApplicationRepository.save(claimApplication);

        createWorkflowHistory(
                claimApplication,
                savedVerification,
                fromStatus,
                claimApplication.getStatus(),
                outcome
        );

        return verificationMapper.toResponse(savedVerification);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimApplicationVerificationResponseDto getById(Long id) {

        ClaimApplicationVerification entity = verificationRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application verification not found with id: " + id
                ));

        return verificationMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplicationVerificationResponseDto> getByClaimApplicationId(
            Long claimApplicationId
    ) {

        if (claimApplicationId == null) {
            throw ClaimException.badRequest("Claim application id is required.");
        }

        return verificationRepository
                .findAllByClaimApplication_IdOrderByVerificationLevelAsc(claimApplicationId)
                .stream()
                .map(verificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimApplicationVerificationResponseDto> getAll() {

        return verificationRepository.findAll()
                .stream()
                .map(verificationMapper::toResponse)
                .toList();
    }

    private ClaimApplicationVerification buildEntity(
            ClaimApplicationVerificationRequestDto request,
            ClaimApplication claimApplication
    ) {

        return ClaimApplicationVerification.builder()
                .claimApplication(claimApplication)
                .verificationLevel(request.getVerificationLevel())

                .verificationStatus(getVerificationStatus(request.getVerificationStatusId()))
                .verificationDecision(getDecision(request.getVerificationDecisionId()))
                .finalVerificationDecision(getDecision(request.getFinalVerificationDecisionId()))

                .isEligible(defaultN(request.getIsEligible()))
                .isRuleMatched(defaultN(request.getIsRuleMatched()))
                .isDocumentVerified(defaultN(request.getIsDocumentVerified()))
                .isBankVerified(defaultN(request.getIsBankVerified()))
                .isCalculationVerified(defaultN(request.getIsCalculationVerified()))
                .isDeductionChecked(defaultN(request.getIsDeductionChecked()))
                .requiresRecalculation(defaultN(request.getRequiresRecalculation()))
                .requiresManualReview(defaultN(request.getRequiresManualReview()))

                .returnReason(request.getReturnReason())
                .rejectionReason(request.getRejectionReason())

                .memberReviewStatus(getReviewStatus(request.getMemberReviewStatusId()))
                .bankReviewStatus(getReviewStatus(request.getBankReviewStatusId()))
                .documentReviewStatus(getReviewStatus(request.getDocumentReviewStatusId()))
                .contributionReviewStatus(getReviewStatus(request.getContributionReviewStatusId()))
                .ruleReviewStatus(getReviewStatus(request.getRuleReviewStatusId()))
                .loanReviewStatus(getReviewStatus(request.getLoanReviewStatusId()))
                .deductionReviewStatus(getReviewStatus(request.getDeductionReviewStatusId()))

                .verifierRemarks(request.getVerifierRemarks())
                .verifiedBy(request.getVerifiedBy())
                .verifiedByRole(request.getVerifiedByRole())
                .verifiedAt(now())

                .isActive(request.getIsActive() != null ? request.getIsActive() : ActivityEnum.Y)
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    private void updateEntity(
            ClaimApplicationVerification entity,
            ClaimApplicationVerificationRequestDto request
    ) {

        if (request == null) {
            throw ClaimException.badRequest("Verification request cannot be null.");
        }

        if (request.getVerificationLevel() != null) {
            entity.setVerificationLevel(request.getVerificationLevel());
        }

        if (request.getVerificationStatusId() != null) {
            entity.setVerificationStatus(getVerificationStatus(request.getVerificationStatusId()));
        }

        if (request.getVerificationDecisionId() != null) {
            entity.setVerificationDecision(getDecision(request.getVerificationDecisionId()));
        }

        if (request.getFinalVerificationDecisionId() != null) {
            entity.setFinalVerificationDecision(getDecision(request.getFinalVerificationDecisionId()));
        }

        if (request.getIsEligible() != null) {
            entity.setIsEligible(request.getIsEligible());
        }

        if (request.getIsRuleMatched() != null) {
            entity.setIsRuleMatched(request.getIsRuleMatched());
        }

        if (request.getIsDocumentVerified() != null) {
            entity.setIsDocumentVerified(request.getIsDocumentVerified());
        }

        if (request.getIsBankVerified() != null) {
            entity.setIsBankVerified(request.getIsBankVerified());
        }

        if (request.getIsCalculationVerified() != null) {
            entity.setIsCalculationVerified(request.getIsCalculationVerified());
        }

        if (request.getIsDeductionChecked() != null) {
            entity.setIsDeductionChecked(request.getIsDeductionChecked());
        }

        if (request.getRequiresRecalculation() != null) {
            entity.setRequiresRecalculation(request.getRequiresRecalculation());
        }

        if (request.getRequiresManualReview() != null) {
            entity.setRequiresManualReview(request.getRequiresManualReview());
        }

        if (request.getReturnReason() != null) {
            entity.setReturnReason(request.getReturnReason());
        }

        if (request.getRejectionReason() != null) {
            entity.setRejectionReason(request.getRejectionReason());
        }

        if (request.getMemberReviewStatusId() != null) {
            entity.setMemberReviewStatus(getReviewStatus(request.getMemberReviewStatusId()));
        }

        if (request.getBankReviewStatusId() != null) {
            entity.setBankReviewStatus(getReviewStatus(request.getBankReviewStatusId()));
        }

        if (request.getDocumentReviewStatusId() != null) {
            entity.setDocumentReviewStatus(getReviewStatus(request.getDocumentReviewStatusId()));
        }

        if (request.getContributionReviewStatusId() != null) {
            entity.setContributionReviewStatus(getReviewStatus(request.getContributionReviewStatusId()));
        }

        if (request.getRuleReviewStatusId() != null) {
            entity.setRuleReviewStatus(getReviewStatus(request.getRuleReviewStatusId()));
        }

        if (request.getLoanReviewStatusId() != null) {
            entity.setLoanReviewStatus(getReviewStatus(request.getLoanReviewStatusId()));
        }

        if (request.getDeductionReviewStatusId() != null) {
            entity.setDeductionReviewStatus(getReviewStatus(request.getDeductionReviewStatusId()));
        }

        if (request.getVerifierRemarks() != null) {
            entity.setVerifierRemarks(request.getVerifierRemarks());
        }

        if (request.getVerifiedBy() != null) {
            entity.setVerifiedBy(request.getVerifiedBy());
        }

        if (request.getVerifiedByRole() != null) {
            entity.setVerifiedByRole(request.getVerifiedByRole());
        }

        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        if (request.getUpdatedBy() != null) {
            entity.setUpdatedBy(request.getUpdatedBy());
        }

        entity.setVerifiedAt(now());
    }

    private void validateBasicRequest(
            ClaimApplicationVerificationRequestDto request
    ) {

        if (request == null) {
            throw ClaimException.badRequest("Verification request cannot be null.");
        }

        if (request.getClaimApplicationId() == null) {
            throw ClaimException.badRequest("Claim application id is required.");
        }

        if (request.getFinalVerificationDecisionId() == null) {
            throw ClaimException.badRequest("Final verification decision is required.");
        }

        if (isBlank(request.getVerifiedBy())) {
            throw ClaimException.badRequest("Verifier is required.");
        }

        if (isBlank(request.getVerifiedByRole())) {
            throw ClaimException.badRequest("Verifier role is required.");
        }

        if (!isBlank(request.getReturnReason()) && !isBlank(request.getRejectionReason())) {
            throw ClaimException.badRequest(
                    "Return reason and rejection reason cannot both be provided."
            );
        }
    }

    private void validateClaimCanBeVerified(ClaimApplication claimApplication) {

        if (claimApplication.getStatus() == null) {
            throw ClaimException.badRequest("Claim application status is missing.");
        }

        String statusName = claimApplication.getStatus().getStatusName();

        if (equalsAny(
                statusName,
                "Approved",
                "Rejected",
                "Claimed",
                "Unclaimed"
        )) {
            throw ClaimException.badRequest(
                    "Claim application cannot be verified because it is already in status: "
                            + statusName
            );
        }
    }

    private VerificationOutcome applyVerificationDecision(
            ClaimApplicationVerification entity
    ) {

        DecisionMaster finalDecision = entity.getFinalVerificationDecision();

        if (finalDecision == null || isBlank(finalDecision.getCode())) {
            throw ClaimException.badRequest("Final verification decision is invalid.");
        }

        String decisionCode = finalDecision.getCode();

        if ("VERIFIED".equalsIgnoreCase(decisionCode)) {
            validateVerifiedDecision(entity);
            return VerificationOutcome.VERIFIED;
        }

        if ("RETURNED".equalsIgnoreCase(decisionCode)) {
            validateReturnedDecision(entity);
            return VerificationOutcome.RETURNED;
        }

        if ("REJECTED".equalsIgnoreCase(decisionCode)) {
            validateRejectedDecision(entity);
            return VerificationOutcome.REJECTED;
        }

        throw ClaimException.badRequest(
                "Unsupported final verification decision: " + decisionCode
        );
    }

    private void validateVerifiedDecision(
            ClaimApplicationVerification entity
    ) {

        if (entity.getIsEligible() != ActivityEnum.Y) {
            throw ClaimException.badRequest("Claim cannot be verified because eligibility is not confirmed.");
        }

        if (entity.getIsRuleMatched() != ActivityEnum.Y) {
            throw ClaimException.badRequest("Claim cannot be verified because rule is not matched.");
        }

        if (entity.getIsDocumentVerified() != ActivityEnum.Y) {
            throw ClaimException.badRequest("Claim cannot be verified because documents are not verified.");
        }

        if (entity.getIsBankVerified() != ActivityEnum.Y) {
            throw ClaimException.badRequest("Claim cannot be verified because bank details are not verified.");
        }

        if (entity.getIsCalculationVerified() != ActivityEnum.Y) {
            throw ClaimException.badRequest("Claim cannot be verified because calculation is not verified.");
        }

        if (entity.getRequiresRecalculation() == ActivityEnum.Y) {
            throw ClaimException.badRequest("Claim cannot be verified because recalculation is required.");
        }

        if (entity.getRequiresManualReview() == ActivityEnum.Y) {
            throw ClaimException.badRequest("Claim cannot be verified because manual review is required.");
        }

        validateRequiredReviewAccepted("member", entity.getMemberReviewStatus());
        validateRequiredReviewAccepted("bank", entity.getBankReviewStatus());
        validateRequiredReviewAccepted("document", entity.getDocumentReviewStatus());
        validateRequiredReviewAccepted("contribution", entity.getContributionReviewStatus());
        validateRequiredReviewAccepted("rule", entity.getRuleReviewStatus());

        validateOptionalReviewAcceptedOrNotApplicable("loan", entity.getLoanReviewStatus());
        validateOptionalReviewAcceptedOrNotApplicable("deduction", entity.getDeductionReviewStatus());
    }

    private void validateReturnedDecision(
            ClaimApplicationVerification entity
    ) {

        if (isBlank(entity.getReturnReason())) {
            throw ClaimException.badRequest("Return reason is required when returning claim.");
        }

        if (!isBlank(entity.getRejectionReason())) {
            throw ClaimException.badRequest("Rejection reason should not be provided when returning claim.");
        }
    }

    private void validateRejectedDecision(
            ClaimApplicationVerification entity
    ) {

        if (isBlank(entity.getRejectionReason())) {
            throw ClaimException.badRequest("Rejection reason is required when rejecting claim.");
        }

        if (!isBlank(entity.getReturnReason())) {
            throw ClaimException.badRequest("Return reason should not be provided when rejecting claim.");
        }
    }

    private void updateClaimApplicationStatusAndStage(
            ClaimApplication claimApplication,
            VerificationOutcome outcome
    ) {

        switch (outcome) {
            case VERIFIED -> {
                claimApplication.setStatus(getStatusByName("Approved pending"));
                claimApplication.setCurrentStage(getStageByCode("APPROVER_REVIEW"));
            }

            case RETURNED -> {
                claimApplication.setStatus(getStatusByName("Re-Submit"));
                claimApplication.setCurrentStage(getStageByCode("MEMBER_CORRECTION"));
            }

            case REJECTED -> {
                claimApplication.setStatus(getStatusByName("REJECTED"));
                claimApplication.setStatus(getStatusByName("Rejected"));
            }
        }
    }


    private void createWorkflowHistory(
            ClaimApplication claimApplication,
            ClaimApplicationVerification verification,
            StatusMaster fromStatus,
            StatusMaster toStatus,
            VerificationOutcome outcome
    ) {

        ClaimApplicationWorkflow workflow = ClaimApplicationWorkflow.builder()
                .claimApplication(claimApplication)
                .workflowLevel(verification.getVerificationLevel())
                .workflowStage(claimApplication.getCurrentStage())
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .action(getActionByCode(outcome.getActionCode()))
                .decision(verification.getFinalVerificationDecision())
                .returnReason(verification.getReturnReason())
                .rejectionReason(verification.getRejectionReason())
                .actionBy(verification.getVerifiedBy())
                .actionAt(now())
                .remarks(verification.getVerifierRemarks())
                .build();

        workflowRepository.save(workflow);
    }

    private void validateRequiredReviewAccepted(
            String sectionName,
            ReviewStatusMaster reviewStatus
    ) {

        if (reviewStatus == null || isBlank(reviewStatus.getCode())) {
            throw ClaimException.badRequest(
                    sectionName + " review status is required."
            );
        }

        if (!"ACCEPTED".equalsIgnoreCase(reviewStatus.getCode())) {
            throw ClaimException.badRequest(
                    sectionName + " review must be accepted before verification."
            );
        }
    }

    private void validateOptionalReviewAcceptedOrNotApplicable(
            String sectionName,
            ReviewStatusMaster reviewStatus
    ) {

        if (reviewStatus == null) {
            return;
        }

        String code = reviewStatus.getCode();

        if (!"ACCEPTED".equalsIgnoreCase(code)
                && !"NOT_APPLICABLE".equalsIgnoreCase(code)) {
            throw ClaimException.badRequest(
                    sectionName + " review must be accepted or not applicable before verification."
            );
        }
    }

    private ClaimApplication getClaimApplication(Long id) {
        return claimApplicationRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Claim application not found with id: " + id
                ));
    }

    private VerificationStatusMaster getVerificationStatus(Long id) {
        if (id == null) {
            return null;
        }

        return verificationStatusRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Verification status not found with id: " + id
                ));
    }

    private DecisionMaster getDecision(Long id) {
        if (id == null) {
            return null;
        }

        return decisionRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Decision not found with id: " + id
                ));
    }

    private ReviewStatusMaster getReviewStatus(Long id) {
        if (id == null) {
            return null;
        }

        return reviewStatusRepository.findById(id)
                .orElseThrow(() -> ClaimException.notFound(
                        "Review status not found with id: " + id
                ));
    }

    private StatusMaster getStatusByName(String statusName) {
        return statusRepository.findByStatusName(statusName)
                .orElseThrow(() -> ClaimException.notFound(
                        "Status not found with name: " + statusName
                ));
    }

    private StageMaster getStageByCode(String code) {
        return stageRepository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Stage not found with code: " + code
                ));
    }

    private ActionMaster getActionByCode(String code) {
        return actionRepository.findByCode(code)
                .orElseThrow(() -> ClaimException.notFound(
                        "Action not found with code: " + code
                ));
    }

    private ActivityEnum defaultN(ActivityEnum value) {
        return value != null ? value : ActivityEnum.N;
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean equalsAny(String value, String... options) {
        if (value == null) {
            return false;
        }

        for (String option : options) {
            if (value.equalsIgnoreCase(option)) {
                return true;
            }
        }

        return false;
    }

    private enum VerificationOutcome {

        VERIFIED("VERIFY"),
        RETURNED("RETURN"),
        REJECTED("REJECT");

        private final String actionCode;

        VerificationOutcome(String actionCode) {
            this.actionCode = actionCode;
        }

        public String getActionCode() {
            return actionCode;
        }
    }
}