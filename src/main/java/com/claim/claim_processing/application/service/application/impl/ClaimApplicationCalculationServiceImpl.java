package com.claim.claim_processing.application.service.application.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationComponentRequestDto;
import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationSummaryRequest;
import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationOtherRequestDto;
import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRuleEvaluationRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationComponent;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationRuleEvaluation;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationCalculationComponentRepository;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationCalculationSummaryRepository;
import com.claim.claim_processing.application.repository.calculation.ClaimApplicationRuleEvaluationRepository;
import com.claim.claim_processing.application.service.application.ClaimApplicationCalculationService;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.SubClaimMappingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimApplicationCalculationServiceImpl implements ClaimApplicationCalculationService {
    
    private final ClaimApplicationCalculationSummaryRepository calculationSummaryRepository;
    private final SubClaimMappingRepository subClaimMappingRepository;
    private final ClaimApplicationRuleEvaluationRepository claimApplicationRuleEvaluationRepository;
    private final ComponentMasterRepository componentMasterRepository;
    private final ClaimApplicationCalculationComponentRepository calculationComponentRepository;

    @Override
    @Transactional
    public ClaimApplicationCalculationSummary initialCreate(ClaimApplication claimApplication,
                            ClaimApplicationOtherRequestDto otherRequest) {
        ClaimApplicationCalculationSummary claimCalculationSummary = ClaimApplicationCalculationSummary
                        .builder()
                        .totalAmount(otherRequest.getTotalAmount())
                        .isPfEligible(otherRequest.getPfIsEligible() != null &&
                                        otherRequest.getPfIsEligible().toString().equals("ELIGIBLE")
                                        ? "Y"
                                        : "N")
                        .isPensionEligible(otherRequest.getPensionIsEligible() != null &&
                                        otherRequest.getPensionIsEligible().toString().equals("ELIGIBLE")
                                        ? "Y"
                                        : "N")
                        .totalContributionMonth(otherRequest.getTotalContributionMonths())
                        .recommendedBenefitType(otherRequest.getRecommendedBenefitType())
                        .totalNonContributionMonth(otherRequest.getTotalNonContributionMonths())
                        .totalPfAmount(otherRequest.getTotalPfAmount())
                        .totalPensionAmount(otherRequest.getTotalPensionAmount())
                        .totalPfInterest(otherRequest.getTotalPfInterest())
                        .totalPensionInterest(otherRequest.getTotalPensionInterest())
                        .createdBy(claimApplication.getCreatedBy())
                        .build();
        claimCalculationSummary.setClaimApplication(claimApplication);
        return calculationSummaryRepository.saveAndFlush(claimCalculationSummary);
    }

    @Override
    @Transactional
    public ClaimApplicationCalculationSummary createForCalculation(ClaimApplication claimApplication,
                    ClaimApplicationCalculationSummaryRequest request) {
        
        log.info("Creating/updating calculation summary for claim application: {}", claimApplication.getId());
        
        // 1. Get or create the summary
        ClaimApplicationCalculationSummary claimCalculationSummary = calculationSummaryRepository
                        .findByClaimApplication_Id(claimApplication.getId()).orElse(null);
        
        if (claimCalculationSummary == null) {
            claimCalculationSummary = ClaimApplicationCalculationSummary
                            .builder()
                            .finalPayableAmount(request.getFinalPayableAmount())
                            .totalAmount(request.getTotalAmount())
                            .isPfEligible(request.getIsPfEligible())
                            .isPensionEligible(request.getIsPensionEligible())
                            .totalContributionMonth(request.getTotalContributionMonth())
                            .totalNonContributionMonth(request.getTotalNonContributionMonth())
                            .totalPfAmount(request.getTotalPfAmount())
                            .totalPensionAmount(request.getTotalPensionAmount())
                            .totalPfInterest(request.getTotalPfInterest())
                            .totalPensionInterest(request.getTotalPensionInterest())
                            .recommendedBenefitType(request.getRecommendedBenefitType())
                            .createdBy(request.getCreatedBy())
                            .build();
            claimCalculationSummary.setClaimApplication(claimApplication);
        } else {
            // Update existing summary
            claimCalculationSummary.setFinalPayableAmount(request.getFinalPayableAmount());
            claimCalculationSummary.setTotalAmount(request.getTotalAmount());
            claimCalculationSummary.setIsPfEligible(request.getIsPfEligible());
            claimCalculationSummary.setIsPensionEligible(request.getIsPensionEligible());
            claimCalculationSummary.setTotalContributionMonth(request.getTotalContributionMonth());
            claimCalculationSummary.setTotalNonContributionMonth(request.getTotalNonContributionMonth());
            claimCalculationSummary.setTotalPfAmount(request.getTotalPfAmount());
            claimCalculationSummary.setTotalPensionAmount(request.getTotalPensionAmount());
            claimCalculationSummary.setTotalPfInterest(request.getTotalPfInterest());
            claimCalculationSummary.setTotalPensionInterest(request.getTotalPensionInterest());
            claimCalculationSummary.setRecommendedBenefitType(request.getRecommendedBenefitType());
            claimCalculationSummary.setUpdatedBy(request.getCreatedBy());
        }

        // 2. Save the summary first
        claimCalculationSummary = calculationSummaryRepository.saveAndFlush(claimCalculationSummary);
        log.info("Saved calculation summary with ID: {}", claimCalculationSummary.getId());

        // 3. Process rule evaluations
        if (request.getRuleEvaluations() != null && !request.getRuleEvaluations().isEmpty()) {
            storeClaimApplicationRuleEvaluation(claimCalculationSummary, request.getRuleEvaluations());
        }
        
        return claimCalculationSummary;
    }

    @Transactional
private void storeClaimApplicationRuleEvaluation(
                ClaimApplicationCalculationSummary claimCalculationSummary,
                List<ClaimApplicationRuleEvaluationRequestDto> requests) {

    log.info("Processing {} rule evaluations for summary ID: {}", 
            requests.size(), claimCalculationSummary.getId());

    for (ClaimApplicationRuleEvaluationRequestDto request : requests) {
        
        SubClaimMapping subRule = subClaimMappingRepository
                        .findBySubClaimCodeIgnoreCase(request.getSubRuleCode())
                        .orElse(null);
        
        ClaimApplicationRuleEvaluation ruleEvaluation = claimApplicationRuleEvaluationRepository
            .findById(request.getRuleEvaluationId())
            .orElse(null);
        
        if (ruleEvaluation == null) {
            // CREATE NEW
            ruleEvaluation = ClaimApplicationRuleEvaluation.builder()
                        .calculationSummary(claimCalculationSummary)
                        .subRule(subRule)
                        .subRuleCode(request.getSubRuleCode())
                        .isRuleApplied(claimCalculationSummary.getClaimApplication().getIsSpecialCase()
                                        .equals(ActivityEnum.Y) ? ActivityEnum.N : ActivityEnum.Y)
                        .resultMessage(request.getResultMessage())
                        .remarks(request.getRemarks())
                        .evaluatedBy(request.getEvaluatedBy() != null ? 
                                request.getEvaluatedBy() : claimCalculationSummary.getCreatedBy())
                        .evaluatedAt(request.getEvaluatedAt() != null ? 
                                request.getEvaluatedAt() : new Timestamp(System.currentTimeMillis()))
                        .createdBy(claimCalculationSummary.getCreatedBy())
                        .build();

            // Build components and set them (new collection is fine for new entity)
            if (request.getComponents() != null && !request.getComponents().isEmpty()) {
                List<ClaimApplicationCalculationComponent> components = 
                        buildComponentsForRuleEvaluation(ruleEvaluation, request.getComponents());
                // For new entity, setting the collection is fine
                ruleEvaluation.setComponents(components);
            }
            claimApplicationRuleEvaluationRepository.saveAndFlush(ruleEvaluation);
            
        } else {
            // UPDATE EXISTING - MODIFY THE COLLECTION, DON'T REPLACE IT
            
            // Update basic fields
            ruleEvaluation.setCalculationSummary(claimCalculationSummary);
            ruleEvaluation.setSubRule(subRule);
            ruleEvaluation.setSubRuleCode(request.getSubRuleCode());
            ruleEvaluation.setIsRuleApplied(claimCalculationSummary.getClaimApplication().getIsSpecialCase()
                            .equals(ActivityEnum.Y) ? ActivityEnum.N : ActivityEnum.Y);
            ruleEvaluation.setResultMessage(request.getResultMessage());
            ruleEvaluation.setRemarks(request.getRemarks());
            ruleEvaluation.setEvaluatedBy(request.getEvaluatedBy() != null ? 
                    request.getEvaluatedBy() : claimCalculationSummary.getCreatedBy());
            ruleEvaluation.setEvaluatedAt(request.getEvaluatedAt() != null ? 
                    request.getEvaluatedAt() : new Timestamp(System.currentTimeMillis()));
            ruleEvaluation.setUpdatedBy(claimCalculationSummary.getCreatedBy());

            // Handle components - MODIFY existing collection
            if (request.getComponents() != null && !request.getComponents().isEmpty()) {
                // Build new components list
                List<ClaimApplicationCalculationComponent> newComponents = 
                        buildComponentsForRuleEvaluation(ruleEvaluation, request.getComponents());
                
                // Use the helper method or modify the existing collection directly
                // OPTION 1: Use the helper method from the entity
                // ruleEvaluation.clearComponents();  // You'd need to add this method
                // ruleEvaluation.addAllComponents(newComponents);  // You'd need to add this method
                
                // OPTION 2: Modify the existing collection directly (RECOMMENDED)
                ruleEvaluation.getComponents().clear();      // Clear existing - this marks them for deletion
                ruleEvaluation.getComponents().addAll(newComponents);  // Add new ones
                
                // IMPORTANT: Ensure each component has the ruleEvaluation set
                // Your buildComponentsForRuleEvaluation already sets this, but we'll double-check
                for (ClaimApplicationCalculationComponent comp : newComponents) {
                    comp.setRuleEvaluation(ruleEvaluation);
                }
                
            } else {
                // If no components in request, clear existing
                ruleEvaluation.getComponents().clear();
            }
            
            claimApplicationRuleEvaluationRepository.saveAndFlush(ruleEvaluation);
        }
    }
}

    @Transactional
    private List<ClaimApplicationCalculationComponent> buildComponentsForRuleEvaluation(
                    ClaimApplicationRuleEvaluation ruleEvaluation,
                    List<ClaimApplicationCalculationComponentRequestDto> componentRequests) {

        List<ClaimApplicationCalculationComponent> components = new ArrayList<>();

        for (ClaimApplicationCalculationComponentRequestDto componentRequest : componentRequests) {
            // Get component master by code
            ComponentMaster componentMaster = componentMasterRepository
                            .findByCode(componentRequest.getComponentCode())
                            .orElseThrow(() -> ClaimException.notFound(
                                    "Component not found with code: " + componentRequest.getComponentCode()));

            // Check if component already exists (for updates)
            ClaimApplicationCalculationComponent component = null;
            
            if (componentRequest.getCalculationComponentId() != null && 
                componentRequest.getCalculationComponentId() > 0) {
                component = calculationComponentRepository
                        .findById(componentRequest.getCalculationComponentId())
                        .orElse(null);
            }

            if (component == null) {
                // Create new component with BOTH componentCode and componentMaster
                component = ClaimApplicationCalculationComponent.builder()
                                .componentCode(componentRequest.getComponentCode())  // Set the code directly
                                .componentMaster(componentMaster)                    // Set the master entity
                                .amount(componentRequest.getAmount())
                                .notes(componentRequest.getNotes())
                                .ruleEvaluation(ruleEvaluation)
                                .isDeduction(ActivityEnum.N)
                                .isActive(ActivityEnum.Y)
                                .createdBy(ruleEvaluation.getCreatedBy())
                                .build();
            } else {
                // Update existing component with BOTH fields
                component.setComponentCode(componentRequest.getComponentCode());  // Update the code
                component.setComponentMaster(componentMaster);                    // Update the master
                component.setAmount(componentRequest.getAmount());
                component.setNotes(componentRequest.getNotes());
                component.setRuleEvaluation(ruleEvaluation);
                component.setUpdatedBy(ruleEvaluation.getCreatedBy());
            }

            components.add(component);
        }

        return components;
    }

    @Override
    @Transactional
    public ClaimApplicationCalculationSummary patch(long calculationId,
                    ClaimApplicationCalculationSummaryRequest request) {

        log.info("Patching calculation summary with ID: {}", calculationId);

        ClaimApplicationCalculationSummary claimCalculationSummary = calculationSummaryRepository
                        .findById(calculationId)
                        .orElseThrow(() -> new RuntimeException(
                                        "Calculation summary not found with id: " + calculationId));

        // Update fields
        if (request.getFinalPayableAmount() != null) {
            claimCalculationSummary.setFinalPayableAmount(request.getFinalPayableAmount());
        }
        if (request.getTotalAmount() != null) {
            claimCalculationSummary.setTotalAmount(request.getTotalAmount());
        }
        if (request.getIsPfEligible() != null) {
            claimCalculationSummary.setIsPfEligible(request.getIsPfEligible());
        }
        if (request.getIsPensionEligible() != null) {
            claimCalculationSummary.setIsPensionEligible(request.getIsPensionEligible());
        }
        if (request.getTotalContributionMonth() != null) {
            claimCalculationSummary.setTotalContributionMonth(request.getTotalContributionMonth());
        }
        if (request.getTotalNonContributionMonth() != null) {
            claimCalculationSummary.setTotalNonContributionMonth(request.getTotalNonContributionMonth());
        }
        if (request.getTotalPfAmount() != null) {
            claimCalculationSummary.setTotalPfAmount(request.getTotalPfAmount());
        }
        if (request.getTotalPensionAmount() != null) {
            claimCalculationSummary.setTotalPensionAmount(request.getTotalPensionAmount());
        }
        if (request.getTotalPfInterest() != null) {
            claimCalculationSummary.setTotalPfInterest(request.getTotalPfInterest());
        }
        if (request.getTotalPensionInterest() != null) {
            claimCalculationSummary.setTotalPensionInterest(request.getTotalPensionInterest());
        }
        if (request.getRecommendedBenefitType() != null) {
            claimCalculationSummary.setRecommendedBenefitType(request.getRecommendedBenefitType());
        }
        
        claimCalculationSummary.setUpdatedBy(request.getCreatedBy());

        // If rule evaluations are provided, update them
        if (request.getRuleEvaluations() != null && !request.getRuleEvaluations().isEmpty()) {
            // Clear existing rule evaluations
            claimCalculationSummary.getRuleEvaluations().clear();
            
            // Add new rule evaluations
            storeClaimApplicationRuleEvaluation(claimCalculationSummary, request.getRuleEvaluations());
        }

        return calculationSummaryRepository.save(claimCalculationSummary);
    }
}