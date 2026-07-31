package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationRuleEvaluationListDto;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationRuleEvaluation;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationCalculationComponentRepository;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationRuleEvaluationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ClaimApplicationCalculationSummaryResponseMapper {
    
    private final ClaimApplicationRuleEvaluationRepository claimApplicationRuleEvaluationRepository;   
    private final ClaimApplicationCalculationComponentRepository claimApplicationCalculationComponentRepository;   

    public ClaimApplicationCalculationSummaryResponseDto toResponse(
            ClaimApplicationCalculationSummary entity) {

        if (entity == null) {
            log.warn("ClaimApplicationCalculationSummary entity is null");
            return null;
        }
        
        try {
            List<ClaimApplicationRuleEvaluation> ruleEvaluations = claimApplicationRuleEvaluationRepository
                    .findByCalculationSummary_Id(entity.getId());
            
            return ClaimApplicationCalculationSummaryResponseDto.builder()
                    .id(entity.getId())
                    .claimApplicationId(
                            entity.getClaimApplication() != null 
                                    ? entity.getClaimApplication().getId() 
                                    : null
                    )
                    .calculationEffectiveDate(entity.getCalculationEffectiveDate())
                    .finalPayableAmount(
                            entity.getFinalPayableAmount() != null 
                                    ? entity.getFinalPayableAmount() 
                                    : BigDecimal.ZERO
                    )
                    .totalAmount(
                            entity.getTotalAmount() != null 
                                    ? entity.getTotalAmount() 
                                    : BigDecimal.ZERO
                    )
                    .isPfEligible(
                            entity.getIsPfEligible() != null 
                                    ? entity.getIsPfEligible() 
                                    : "N"
                    )
                    .isPensionEligible(
                            entity.getIsPensionEligible() != null 
                                    ? entity.getIsPensionEligible() 
                                    : "N"
                    )
                    .totalContributionMonth(
                            entity.getTotalContributionMonth() != null 
                                    ? entity.getTotalContributionMonth() 
                                    : 0
                    )
                    .totalNonContributionMonth(
                            entity.getTotalNonContributionMonth() != null 
                                    ? entity.getTotalNonContributionMonth() 
                                    : 0
                    )
                    .totalPfAmount(
                            entity.getTotalPfAmount() != null 
                                    ? entity.getTotalPfAmount() 
                                    : BigDecimal.ZERO
                    )
                    .totalPensionAmount(
                            entity.getTotalPensionAmount() != null 
                                    ? entity.getTotalPensionAmount() 
                                    : BigDecimal.ZERO
                    )
                    .totalPfInterest(
                            entity.getTotalPfInterest() != null 
                                    ? entity.getTotalPfInterest() 
                                    : BigDecimal.ZERO
                    )
                    .totalPensionInterest(
                            entity.getTotalPensionInterest() != null 
                                    ? entity.getTotalPensionInterest() 
                                    : BigDecimal.ZERO
                    )
                    .recommendedBenefitType(
                            entity.getRecommendedBenefitType() != null 
                                    ? entity.getRecommendedBenefitType() 
                                    : "NOT_DETERMINED"
                    )
                    .excessOpeningBalance(
                            entity.getExcessOpeningBalance() != null 
                                    ? entity.getExcessOpeningBalance() 
                                    : BigDecimal.ZERO
                    )
                    .excessServiceAmount(
                            entity.getExcessServiceAmount() != null 
                                    ? entity.getExcessServiceAmount() 
                                    : BigDecimal.ZERO
                    )
                    .excessCutoffDate(entity.getExcessCutoffDate())
                    .excessStartDate(entity.getExcessStartDate())
                    .excessEndDate(entity.getExcessEndDate())
                    .excessTotalContributions(
                            entity.getExcessTotalContributions() != null 
                                    ? entity.getExcessTotalContributions() 
                                    : BigDecimal.ZERO
                    )
                    .excessTotalInterest(
                            entity.getExcessTotalInterest() != null 
                                    ? entity.getExcessTotalInterest() 
                                    : BigDecimal.ZERO
                    )
                    .excessEolMonths(
                            entity.getExcessEolMonths() != null 
                                    ? entity.getExcessEolMonths() 
                                    : 0
                    )
                    .ruleEvaluations(
                            ruleEvaluations != null && !ruleEvaluations.isEmpty()
                                    ? ruleEvaluations.stream()
                                            .filter(Objects::nonNull)
                                            .map(this::mapRuleEvaluation)
                                            .filter(Objects::nonNull)
                                            .toList()
                                    : List.of()
                    )
                    .createdBy(
                            entity.getCreatedBy() != null 
                                    ? entity.getCreatedBy() 
                                    : "SYSTEM"
                    )
                    .createdAt(
                            entity.getCreatedAt() != null 
                                    ? entity.getCreatedAt().toLocalDateTime() 
                                    : null
                    )
                    .updatedBy(
                            entity.getUpdatedBy() != null 
                                    ? entity.getUpdatedBy() 
                                    : null
                    )
                    .updatedAt(
                            entity.getUpdatedAt() != null 
                                    ? entity.getUpdatedAt().toLocalDateTime() 
                                    : null
                    )
                    .build();
                    
        } catch (Exception e) {
            log.error("Error mapping ClaimApplicationCalculationSummary to DTO: {}", e.getMessage(), e);
            return null;
        }
    }

    private ClaimApplicationRuleEvaluationListDto mapRuleEvaluation(
            ClaimApplicationRuleEvaluation rule) {

        if (rule == null) {
            log.warn("ClaimApplicationRuleEvaluation is null");
            return null;
        }

        try {
            List<ClaimApplicationCalculationComponent> components = 
                    claimApplicationCalculationComponentRepository
                            .findByRuleEvaluation_Id(rule.getId());
            
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
                    .isRuleApplied(
                            rule.getIsRuleApplied() != null 
                                    ? rule.getIsRuleApplied() 
                                    : null
                    )
                    .evaluatedBy(
                            rule.getEvaluatedBy() != null 
                                    ? rule.getEvaluatedBy() 
                                    : "SYSTEM"
                    )
                    .evaluatedAt(
                            rule.getEvaluatedAt() != null 
                                    ? rule.getEvaluatedAt().toLocalDateTime() 
                                    : null
                    )
                    .remarks(
                            rule.getRemarks() != null 
                                    ? rule.getRemarks() 
                                    : null
                    )
                    .resultMessage(
                            rule.getResultMessage() != null 
                                    ? rule.getResultMessage() 
                                    : null
                    )
                    .components(
                            components != null && !components.isEmpty()
                                    ? components.stream()
                                            .filter(Objects::nonNull)
                                            .map(this::mapCalculationComponent)
                                            .filter(Objects::nonNull)
                                            .toList()
                                    : List.of()
                    )
                    .build();
                    
        } catch (Exception e) {
            log.error("Error mapping ClaimApplicationRuleEvaluation to DTO: {}", e.getMessage(), e);
            return null;
        }
    }

    private ClaimApplicationCalculationComponentDto mapCalculationComponent(
            ClaimApplicationCalculationComponent entity) {

        if (entity == null) {
            log.warn("ClaimApplicationCalculationComponent is null");
            return null;
        }

        try {
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
                    .amount(
                            entity.getAmount() != null 
                                    ? entity.getAmount() 
                                    : BigDecimal.ZERO
                    )
                    .isDeduction(
                            entity.getIsDeduction() != null 
                                    ? entity.getIsDeduction() 
                                    : null
                    )
                    .notes(
                            entity.getNotes() != null 
                                    ? entity.getNotes() 
                                    : null
                    )
                    .isActive(
                            entity.getIsActive() != null 
                                    ? entity.getIsActive() 
                                    : null
                    )
                    .createdBy(
                            entity.getCreatedBy() != null 
                                    ? entity.getCreatedBy() 
                                    : "SYSTEM"
                    )
                    .createdAt(
                            entity.getCreatedAt() != null 
                                    ? entity.getCreatedAt().toLocalDateTime() 
                                    : null
                    )
                    .updatedBy(
                            entity.getUpdatedBy() != null 
                                    ? entity.getUpdatedBy() 
                                    : null
                    )
                    .updatedAt(
                            entity.getUpdatedAt() != null 
                                    ? entity.getUpdatedAt().toLocalDateTime() 
                                    : null
                    )
                    .build();
                    
        } catch (Exception e) {
            log.error("Error mapping ClaimApplicationCalculationComponent to DTO: {}", e.getMessage(), e);
            return null;
        }
    }
}