package com.claim.claim_processing.application.mapper.workFlow;

import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;
import com.claim.claim_processing.common.mapper.common.DecisionMapper;
import com.claim.claim_processing.common.mapper.common.ReviewStatusMapper;
import com.claim.claim_processing.common.mapper.statusMaster.VerificationStatusMasterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimApplicationVerificationMapper {

    private final DecisionMapper decisionMapper;
    private final ReviewStatusMapper reviewStatusMapper;
    private final VerificationStatusMasterMapper verificationStatusMapper;

    public ClaimApplicationVerificationResponseDto toResponse(
            ClaimApplicationVerification entity
    ) {
        if (entity == null) {
            return null;
        }

        return ClaimApplicationVerificationResponseDto.builder()
                .id(entity.getId())
                .claimApplicationId(
                        entity.getClaimApplication() != null
                                ? entity.getClaimApplication().getId()
                                : null
                )
                .verificationLevel(entity.getVerificationLevel())

                .verificationDecision(
                        decisionMapper.toResponseDto(entity.getVerificationDecision())
                )
                .verificationStatus(
                        verificationStatusMapper.toResponseDto(entity.getVerificationStatus())
                )

                .isEligible(entity.getIsEligible())
                .isRuleMatched(entity.getIsRuleMatched())
                .isDocumentVerified(entity.getIsDocumentVerified())
                .isBankVerified(entity.getIsBankVerified())
                .isCalculationVerified(entity.getIsCalculationVerified())
                .isDeductionChecked(entity.getIsDeductionChecked())
                .requiresRecalculation(entity.getRequiresRecalculation())
                .requiresManualReview(entity.getRequiresManualReview())

                .returnReason(entity.getReturnReason())
                .rejectionReason(entity.getRejectionReason())

                .memberReviewStatus(
                        reviewStatusMapper.toResponseDto(entity.getMemberReviewStatus())
                )
                .bankReviewStatus(
                        reviewStatusMapper.toResponseDto(entity.getBankReviewStatus())
                )
                .documentReviewStatus(
                        reviewStatusMapper.toResponseDto(entity.getDocumentReviewStatus())
                )
                .contributionReviewStatus(
                        reviewStatusMapper.toResponseDto(entity.getContributionReviewStatus())
                )
                .ruleReviewStatus(
                        reviewStatusMapper.toResponseDto(entity.getRuleReviewStatus())
                )
                .loanReviewStatus(
                        reviewStatusMapper.toResponseDto(entity.getLoanReviewStatus())
                )
                .deductionReviewStatus(
                        reviewStatusMapper.toResponseDto(entity.getDeductionReviewStatus())
                )

                .finalVerificationDecision(
                        decisionMapper.toResponseDto(entity.getFinalVerificationDecision())
                )

                .verifierRemarks(entity.getVerifierRemarks())
                .verifiedBy(entity.getVerifiedBy())
                .verifiedByRole(entity.getVerifiedByRole())
                .verifiedAt(
                        entity.getVerifiedAt() != null
                                ? entity.getVerifiedAt().toLocalDateTime()
                                : null
                )

                .isActive(
                        entity.getIsActive() != null
                                ? entity.getIsActive().name()
                                : null
                )

                .createdBy(entity.getCreatedBy())
                .createdAt(
                        entity.getCreatedAt() != null
                                ? entity.getCreatedAt().toLocalDateTime()
                                : null
                )
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(
                        entity.getUpdatedAt() != null
                                ? entity.getUpdatedAt().toLocalDateTime()
                                : null
                )
                .build();
    }
}