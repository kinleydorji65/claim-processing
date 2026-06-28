package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import java.util.List;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationRuleEvaluationListDto;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationRuleEvaluation;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationCalculationComponentRepository;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationRuleEvaluationRepository;
import com.claim.claim_processing.common.DTO.response.others.StatusMasterResponseDto;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ClaimApplicationCalculationSummaryResponseMapper {
    public final ClaimApplicationRuleEvaluationRepository claimApplicationRuleEvaluationRepository;   
    public final ClaimApplicationCalculationComponentRepository claimApplicationCalculationComponentRepository;   

    public ClaimApplicationCalculationSummaryResponseDto toResponse(
            ClaimApplicationCalculationSummary entity
    ) {

        if (entity == null) {
            return null;
        }
        List<ClaimApplicationRuleEvaluation> ruleEvaluations = claimApplicationRuleEvaluationRepository
                .findByCalculationSummary_Id(entity.getId());
        return ClaimApplicationCalculationSummaryResponseDto.builder()
                .id(entity.getId())

                .claimApplicationId(
                        entity.getClaimApplication() != null
                                ? entity.getClaimApplication().getId()
                                : null
                )

                .finalPayableAmount(entity.getFinalPayableAmount())
                .actualAmountCalculated(entity.getActualAmountCalculated())
                .totalAmount(entity.getTotalAmount())

                .isPfEligible(entity.getIsPfEligible())
                .isPensionEligible(entity.getIsPensionEligible())

                .totalContributionMonth(entity.getTotalContributionMonth())
                .recommendedBenefitType(entity.getRecommendedBenefitType())

                .calculationStatus(
                        entity.getCalculationStatus() != null
                                ? StatusMasterResponseDto.builder()
                                .statusId(entity.getCalculationStatus().getStatusId())
                                .statusName(entity.getCalculationStatus().getStatusName())
                                .build()
                                : null
                )

                .ruleEvaluations(
                        ruleEvaluations == null
                                ? List.of()
                                : ruleEvaluations
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
        List<ClaimApplicationCalculationComponent> components = claimApplicationCalculationComponentRepository.findByRuleEvaluation_Id(rule.getId());
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

                .components(
                        components == null
                                ? List.of()
                                : components
                                .stream()
                                .map(this::mapCalculationComponent)
                                .toList()
                )

                .build();
    }

    private ClaimApplicationCalculationComponentDto mapCalculationComponent(
        ClaimApplicationCalculationComponent entity) {

    if (entity == null) return null;

    return ClaimApplicationCalculationComponentDto.builder()
            .id(entity.getId())
            .ruleEvaluationId(
                    entity.getRuleEvaluation() != null
                            ? entity.getRuleEvaluation().getId()
                            : null
            )
            .componentCode(
                    entity.getComponentMaster() != null
                            ? entity.getComponentMaster().getCode()
                            : null
            )
            .componentName(
                    entity.getComponentMaster() != null
                            ? entity.getComponentMaster().getName()
                            : null
            )
            .amount(entity.getAmount())
            .isDeduction(entity.getIsDeduction())
            .notes(entity.getNotes())
            .isActive(entity.getIsActive())
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
