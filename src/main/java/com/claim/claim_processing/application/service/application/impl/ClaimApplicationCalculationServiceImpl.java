package com.claim.claim_processing.application.service.application.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationPatchRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationRuleEvaluation;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationCalculationSummaryRepository;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationRuleEvaluationRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationCalculationService;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.SubClaimMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimApplicationCalculationServiceImpl implements ClaimApplicationCalculationService {
        private final StatusMasterRepository statusRepository;
        private final ClaimApplicationCalculationSummaryRepository calculationSummaryRepository;
        private final SubClaimMappingRepository subClaimMappingRepository;
        private final ClaimApplicationRuleEvaluationRepository claimApplicationRuleEvaluationRepository;
        private final ComponentMasterRepository componentMasterRepository;

        @Override
        public ClaimApplicationCalculationSummary create(ClaimApplication claimApplication,
                        ClaimCalculationResponseDTO calculationResponse, BigDecimal finalPayableAmount) {

                ClaimApplicationCalculationSummary claimCalculationSummary = ClaimApplicationCalculationSummary
                                .builder()
                                .finalPayableAmount(finalPayableAmount)
                                .actualAmountCalculated(calculationResponse.getFinalPayableAmount())
                                .isPfEligible(calculationResponse.getPfIsEligible() != null &&
                                                calculationResponse.getPfIsEligible().toString().equals("ELIGIBLE")
                                                                ? "Y"
                                                                : "N")
                                .isPensionEligible(calculationResponse.getPensionIsEligible() != null &&
                                                calculationResponse.getPensionIsEligible().toString().equals("ELIGIBLE")
                                                                ? "Y"
                                                                : "N")
                                .totalContributionMonth(calculationResponse.getTotalContributionMonths())
                                .recommendedBenefitType(calculationResponse.getRecommendedBenefitType())
                                .isActive(ActivityEnum.Y)
                                .createdBy(claimApplication.getCreatedBy())
                                .build();
                claimCalculationSummary.setClaimApplication(claimApplication);
                calculationSummaryRepository.saveAndFlush(claimCalculationSummary);
                storeClaimApplicationRuleEvaluation(claimCalculationSummary, calculationResponse);
                return claimCalculationSummary;
        }

        private void storeClaimApplicationRuleEvaluation(ClaimApplicationCalculationSummary claimCalculationSummary,
                        ClaimCalculationResponseDTO calculationResponse) {
                List<SubClaimMapping> subRuleList = subClaimMappingRepository
                                .findByRuleType_CodeIgnoreCase(calculationResponse.getSubClaimCode()).stream().toList();
                subRuleList.stream()
                                .map(m -> {
                                        ClaimApplicationRuleEvaluation ruleEvaluation = ClaimApplicationRuleEvaluation
                                                        .builder()
                                                        .calculationSummary(claimCalculationSummary)
                                                        .isRuleApplied(claimCalculationSummary.getClaimApplication()
                                                                        .getIsSpecialCase()
                                                                        .equals(ActivityEnum.Y)
                                                                                        ? ActivityEnum.N
                                                                                        : ActivityEnum.Y)
                                                        .resultMessage(claimCalculationSummary.getClaimApplication()
                                                                        .getIsSpecialCase()
                                                                        .equals(ActivityEnum.Y)
                                                                                        ? "Special case - Rule not applied"
                                                                                        : "Rule applied successfully")
                                                        .evaluatedBy(claimCalculationSummary.getCreatedBy())
                                                        .subRule(m)
                                                        .evaluatedAt(new Timestamp(System.currentTimeMillis()))
                                                        .isActive(claimCalculationSummary.getIsActive())
                                                        .createdBy(claimCalculationSummary.getCreatedBy())
                                                        .build();
                                        claimApplicationRuleEvaluationRepository.saveAndFlush(ruleEvaluation);

                                        storeClaimApplicationCalculationComponents(ruleEvaluation, calculationResponse);
                                        return ruleEvaluation;
                                }).toList();
        }

        private void storeClaimApplicationCalculationComponents(ClaimApplicationRuleEvaluation ruleEvaluation,
                        ClaimCalculationResponseDTO calculationResponse) {
                calculationResponse.getComponents().forEach(component -> {
                        ComponentMaster componentMaster = componentMasterRepository.findByCode(component.getCode())
                                        .orElseThrow(
                                                        () -> ClaimException.notFound("Component not found with code: "
                                                                        + component.getCode()));
                        ruleEvaluation.getComponents().add(
                                        ClaimApplicationCalculationComponent.builder()
                                                        .componentMaster(componentMaster)
                                                        .amount(component.getAmount())
                                                        .isActive(ruleEvaluation.getIsActive())
                                                        .createdBy(ruleEvaluation.getCreatedBy())
                                                        .build());
                });
                claimApplicationRuleEvaluationRepository.saveAndFlush(ruleEvaluation);
        }

        // patch
        @Override
        public ClaimApplicationCalculationSummary patch(ClaimApplicationCalculationPatchRequestDto request) {

                ClaimApplicationCalculationSummary summary = calculationSummaryRepository
                                .findById(request.getCalculationSummaryId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Calculation summary not found with id: "
                                                                + request.getCalculationSummaryId()));

                if (request.getFinalPayableAmount() != null) {
                        summary.setFinalPayableAmount(request.getFinalPayableAmount());
                }

                if (request.getActualAmountCalculated() != null) {
                        summary.setActualAmountCalculated(request.getActualAmountCalculated());
                }

                if (request.getCalculationStatusId() != null) {
                        StatusMaster status = statusRepository.findById(request.getCalculationStatusId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Status not found with id: "
                                                                        + request.getCalculationStatusId()));

                        summary.setCalculationStatus(status);
                }

                if (request.getRecommendedBenefitType() != null) {
                        summary.setRecommendedBenefitType(request.getRecommendedBenefitType());
                }

                if (request.getUpdatedBy() != null) {
                        summary.setUpdatedBy(request.getUpdatedBy());
                }

                return calculationSummaryRepository.save(summary);
        }
}
