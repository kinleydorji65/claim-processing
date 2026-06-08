package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import java.util.List;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationRuleEvaluationListDto;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationRuleEvaluation;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.DTO.response.others.StatusMasterResponseDto;

@Component
public class ClaimApplicationCalculationSummaryResponseMapper {

    public ClaimApplicationCalculationSummaryResponseDto toResponse(
            ClaimApplicationCalculationSummary entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationCalculationSummaryResponseDto.builder()
                .id(entity.getId())

                .claimApplicationId(
                        entity.getClaimApplication() != null
                                ? entity.getClaimApplication().getId()
                                : null
                )

                .calculationStage(
                        entity.getCalculationStage() != null
                                ? StageResponseDto.builder()
                                .id(entity.getCalculationStage().getId())
                                .name(entity.getCalculationStage().getName())
                                .build()
                                : null
                )

                .finalPayableAmount(entity.getFinalPayableAmount())
                .actualAmountCalculated(entity.getActualAmountCalculated())

                .isPfEligible(entity.getIsPfEligible())
                .isPensionEligible(entity.getIsPensionEligible())

                .totalContributionMonth(entity.getTotalContributionMonth())
                .recommendedBenefitType(entity.getRecommendedBenefitType())

                .calculationStatus(
                        entity.getCalculationStatus() != null
                                ? StatusMasterResponseDto.builder()
                                .statusId(entity.getCalculationStatus().getStatusId())
                                .statuseName(entity.getCalculationStatus().getStatusName())
                                .build()
                                : null
                )

                .ruleEvaluations(
                        entity.getRuleEvaluations() == null
                                ? List.of()
                                : entity.getRuleEvaluations()
                                .stream()
                                .map(this::mapRuleEvaluation)
                                .toList()
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

    private ClaimApplicationRuleEvaluationListDto mapRuleEvaluation(
            ClaimApplicationRuleEvaluation rule
    ) {

        return ClaimApplicationRuleEvaluationListDto.builder()
                .id(rule.getId())

                .subClaimCode(
                        rule.getSubRule() != null
                                ? rule.getSubRule().getSubClaimCode()
                                : null
                )

                .subClaimType(
                        rule.getSubRule() != null
                                ? rule.getSubRule().getSubClaimType()
                                : null
                )

                .isRuleApplied(rule.getIsRuleApplied())
                .resultMessage(rule.getResultMessage())

                .evaluatedBy(rule.getEvaluatedBy())

                .evaluatedAt(
                        rule.getEvaluatedAt() != null
                                ? rule.getEvaluatedAt().toLocalDateTime()
                                : null
                )

                .isActive(rule.getIsActive())

                .build();
    }
}
