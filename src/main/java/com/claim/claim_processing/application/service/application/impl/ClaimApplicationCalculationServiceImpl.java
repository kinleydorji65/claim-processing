package com.claim.claim_processing.application.service.application.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationOtherRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationRuleEvaluation;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationCalculationSummaryRepository;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationRuleEvaluationRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationCalculationService;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.common.StageRepository;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.SubClaimMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimApplicationCalculationServiceImpl implements ClaimApplicationCalculationService {
    private final BenefitCalculationService benefitCalculationService;
    private final StatusMasterRepository statusRepository;
    private final ClaimApplicationCalculationSummaryRepository calculationSummaryRepository;
    private final StageRepository stageMasterRepository;
    private final SubClaimMappingRepository subClaimMappingRepository;
    private final ClaimApplicationRuleEvaluationRepository claimApplicationRuleEvaluationRepository;
    private final ComponentMasterRepository componentMasterRepository;

    @Override
    public ClaimApplicationCalculationSummary create(ClaimApplication claimApplication,
            ClaimApplicationOtherRequestDto request) {

        ClaimInitialPreviewRequest requestForBenefitCalculation = ClaimInitialPreviewRequest.builder()
                .cessationDate(request.getCessationDate())
                .cessationTypeId(request.getCessationTypeId())
                .claimTypeId(claimApplication.getClaimType().getId())
                .nppfNumber(claimApplication.getNppfNumber())
                .isSpecialCase((claimApplication.getIsSpecialCase().toString() == "Y") ? true : false)
                .reasonTypeId(request.getReasonTypeId())
                .build();
        ClaimCalculationResponseDTO calculationResponse = benefitCalculationService
                .calculateBenefit(requestForBenefitCalculation).getData();

        StatusMaster status = statusRepository.findById(request.getCalculationStatusId())
                .orElseThrow(
                        () -> new RuntimeException("Status not found with id: " + request.getCalculationStatusId()));
        StageMaster stage = stageMasterRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Stage not found with id: " + 1L));
        ClaimApplicationCalculationSummary claimCalculationSummary = ClaimApplicationCalculationSummary.builder()
                .finalPayableAmount(request.getFinalPayableAmount())
                .actualAmountCalculated(calculationResponse.getFinalPayableAmount())
                .isPfEligible(calculationResponse.getPfIsEligible().toString())
                .isPensionEligible(calculationResponse.getPensionIsEligible().toString())
                .totalContributionMonth(calculationResponse.getTotalContributionMonths())
                .recommendedBenefitType(calculationResponse.getRecommendedBenefitType())
                .isActive(ActivityEnum.Y)
                .createdBy(request.getCreatedBy())
                .build();
        claimCalculationSummary.setCalculationStatus(status);
        claimCalculationSummary.setClaimApplication(claimApplication);
        claimCalculationSummary.setCalculationStage(stage);
        calculationSummaryRepository.saveAndFlush(claimCalculationSummary);
        storeClaimApplicationRuleEvaluation(claimCalculationSummary, calculationResponse);
        return claimCalculationSummary;
    }

    private void storeClaimApplicationRuleEvaluation(ClaimApplicationCalculationSummary claimCalculationSummary,
            ClaimCalculationResponseDTO calculationResponse) {
        SubClaimMapping subRule = subClaimMappingRepository
                .findFirstBySubClaimCodeIgnoreCase(calculationResponse.getSubClaimCode()).orElseThrow(() -> ClaimException
                        .notFound("Sub Claim Not found with sub claim code: " + calculationResponse.getSubClaimCode()));
        ClaimApplicationRuleEvaluation ruleEvaluation = ClaimApplicationRuleEvaluation.builder()
                .calculationSummary(claimCalculationSummary)
                .isRuleApplied(claimCalculationSummary.getClaimApplication().getIsSpecialCase().equals(ActivityEnum.Y)
                        ? ActivityEnum.N
                        : ActivityEnum.Y)
                .resultMessage(claimCalculationSummary.getClaimApplication().getIsSpecialCase().equals(ActivityEnum.Y)
                        ? "Special case - Rule not applied"
                        : "Rule applied successfully")
                .evaluatedBy(claimCalculationSummary.getCreatedBy())
                .subRule(subRule)
                .evaluatedAt(new Timestamp(System.currentTimeMillis()))
                .isActive(claimCalculationSummary.getIsActive())
                .createdBy(claimCalculationSummary.getCreatedBy())
                .build();
        claimApplicationRuleEvaluationRepository.saveAndFlush(ruleEvaluation);
        storeClaimApplicationCalculationComponents(ruleEvaluation, calculationResponse);
    }

    private void storeClaimApplicationCalculationComponents(ClaimApplicationRuleEvaluation ruleEvaluation,
            ClaimCalculationResponseDTO calculationResponse) {
        calculationResponse.getComponents().forEach(component -> {
            ComponentMaster componentMaster = componentMasterRepository.findByCode(component.getCode())
                    .orElseThrow(
                            () -> ClaimException.notFound("Component not found with code: " + component.getCode()));
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
}
