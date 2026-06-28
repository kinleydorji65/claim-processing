package com.claim.claim_processing.application.service.application.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
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
                                .totalAmount(calculationResponse.getTotalAmount())
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

        private void storeClaimApplicationRuleEvaluation(
        ClaimApplicationCalculationSummary claimCalculationSummary,
        ClaimCalculationResponseDTO calculationResponse) {

    Map<String, List<ComponentBalanceDTO>> groupedComponents =
            calculationResponse.getComponents()
                    .stream()
                    .collect(Collectors.groupingBy(ComponentBalanceDTO::getSubRuleCode));

    for (Map.Entry<String, List<ComponentBalanceDTO>> entry : groupedComponents.entrySet()) {

        String subRuleCode = entry.getKey();
        List<ComponentBalanceDTO> components = entry.getValue();

        SubClaimMapping subRule = subClaimMappingRepository.findBySubClaimCodeIgnoreCase(subRuleCode).orElseThrow(() -> ClaimException.notFound("SubClaimMapping not found with code: " + subRuleCode));

            ClaimApplicationRuleEvaluation ruleEvaluation =
                    ClaimApplicationRuleEvaluation.builder()
                            .calculationSummary(claimCalculationSummary)
                            .isRuleApplied(
                                    claimCalculationSummary.getClaimApplication().getIsSpecialCase()
                                            .equals(ActivityEnum.Y)
                                            ? ActivityEnum.N
                                            : ActivityEnum.Y
                            )
                            .resultMessage(
                                    claimCalculationSummary.getClaimApplication().getIsSpecialCase()
                                            .equals(ActivityEnum.Y)
                                            ? "Special case - Rule not applied"
                                            : "Rule applied successfully"
                            )
                            .evaluatedBy(claimCalculationSummary.getCreatedBy())
                            .subRule(subRule)
                            .evaluatedAt(new Timestamp(System.currentTimeMillis()))
                            .isActive(claimCalculationSummary.getIsActive())
                            .createdBy(claimCalculationSummary.getCreatedBy())
                            .calculationSummary(claimCalculationSummary)
                            .build();

            // ✅ only pass relevant components (NOT full list)
            storeClaimApplicationCalculationComponents(ruleEvaluation, components);

            claimApplicationRuleEvaluationRepository.save(ruleEvaluation);
        }
    }


        private void storeClaimApplicationCalculationComponents(
        ClaimApplicationRuleEvaluation ruleEvaluation,
        List<ComponentBalanceDTO> components) {

    if (ruleEvaluation.getComponents() == null) {
        ruleEvaluation.setComponents(new ArrayList<>());
    }

    for (ComponentBalanceDTO component : components) {

        ComponentMaster componentMaster =
                componentMasterRepository.findByCode(component.getCode())
                        .orElseThrow(() ->
                                ClaimException.notFound(
                                        "Component not found with code: " + component.getCode()
                                )
                        );

        ruleEvaluation.getComponents().add(
                ClaimApplicationCalculationComponent.builder()
                        .componentMaster(componentMaster)
                        .amount(component.getAmount())
                        .ruleEvaluation(ruleEvaluation)
                        .isActive(ruleEvaluation.getIsActive())
                        .createdBy(ruleEvaluation.getCreatedBy())
                        .build()
        );
    }
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
                
                if (request.getTotalAmount() != null) {
                        summary.setTotalAmount(request.getTotalAmount());
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
