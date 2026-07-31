package com.claim.claim_processing.application.service.application.impl;

import java.math.BigDecimal;
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

    // ==================== HELPER METHODS ====================
    
    private BigDecimal safeBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
    
    private Integer safeInteger(Integer value) {
        return value != null ? value : 0;
    }
    
    private String safeString(String value, String defaultValue) {
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
    
    private String safeString(String value) {
        return value != null ? value : null;
    }

    // ==================== CREATE METHODS ====================

    @Override
@Transactional
public ClaimApplicationCalculationSummary initialCreate(ClaimApplication claimApplication,
                        ClaimApplicationOtherRequestDto otherRequest) {
    
    if (otherRequest == null) {
        log.warn("OtherRequest is null, creating empty summary");
        return null;
    }
    
    // 🔥 FIX: Check for null values
    String pfEligible = "N";
    if (otherRequest.getPfIsEligible() != null && 
        "ELIGIBLE".equals(otherRequest.getPfIsEligible().toString())) {
        pfEligible = "Y";
    }
    
    String pensionEligible = "N";
    if (otherRequest.getPensionIsEligible() != null && 
        "ELIGIBLE".equals(otherRequest.getPensionIsEligible().toString())) {
        pensionEligible = "Y";
    }
    
    ClaimApplicationCalculationSummary claimCalculationSummary = ClaimApplicationCalculationSummary
                    .builder()
                    .totalAmount(safeBigDecimal(otherRequest.getTotalAmount()))
                    .isPfEligible(pfEligible)
                    .isPensionEligible(pensionEligible)
                    .totalContributionMonth(safeInteger(otherRequest.getTotalContributionMonths()))
                    .recommendedBenefitType(safeString(otherRequest.getRecommendedBenefitType()))
                    .totalNonContributionMonth(safeInteger(otherRequest.getTotalNonContributionMonths()))
                    .totalPfAmount(safeBigDecimal(otherRequest.getTotalPfAmount()))
                    .totalPensionAmount(safeBigDecimal(otherRequest.getTotalPensionAmount()))
                    .totalPfInterest(safeBigDecimal(otherRequest.getTotalPfInterest()))
                    .totalPensionInterest(safeBigDecimal(otherRequest.getTotalPensionInterest()))
                    .excessOpeningBalance(BigDecimal.ZERO)
                    .excessServiceAmount(BigDecimal.ZERO)
                    .excessTotalContributions(BigDecimal.ZERO)
                    .excessTotalInterest(BigDecimal.ZERO)
                    .excessEolMonths(0)
                    .createdBy(safeString(claimApplication.getCreatedBy(), "SYSTEM"))
                    .build();
    
    claimCalculationSummary.setClaimApplication(claimApplication);
    return calculationSummaryRepository.saveAndFlush(claimCalculationSummary);
}

    @Override
    @Transactional
    public ClaimApplicationCalculationSummary createForCalculation(ClaimApplication claimApplication,
                    ClaimApplicationCalculationSummaryRequest request) {
        
        log.info("Creating/updating calculation summary for claim application: {}", claimApplication.getId());
        
        // NULL CHECK - Return empty summary if request is null
        if (request == null) {
            log.warn("Request is null, creating empty summary");
            return null;
        }
        
        // 1. Get or create the summary
        ClaimApplicationCalculationSummary claimCalculationSummary = calculationSummaryRepository
                        .findByClaimApplication_Id(claimApplication.getId()).orElse(null);
        
        if (claimCalculationSummary == null) {
            // CREATE NEW
            claimCalculationSummary = ClaimApplicationCalculationSummary.builder()
                    .calculationEffectiveDate(request.getCalculationEffectiveDate())
                    .finalPayableAmount(safeBigDecimal(request.getFinalPayableAmount()))
                    .totalAmount(safeBigDecimal(request.getTotalAmount()))
                    .isPfEligible(safeString(request.getIsPfEligible(), "N"))
                    .isPensionEligible(safeString(request.getIsPensionEligible(), "N"))
                    .totalContributionMonth(safeInteger(request.getTotalContributionMonth()))
                    .totalNonContributionMonth(safeInteger(request.getTotalNonContributionMonth()))
                    .totalPfAmount(safeBigDecimal(request.getTotalPfAmount()))
                    .totalPensionAmount(safeBigDecimal(request.getTotalPensionAmount()))
                    .totalPfInterest(safeBigDecimal(request.getTotalPfInterest()))
                    .totalPensionInterest(safeBigDecimal(request.getTotalPensionInterest()))
                    .recommendedBenefitType(safeString(request.getRecommendedBenefitType()))
                    .createdBy(safeString(request.getCreatedBy(), "SYSTEM"))
                    .excessOpeningBalance(safeBigDecimal(request.getExcessOpeningBalance()))
                    .excessServiceAmount(safeBigDecimal(request.getExcessServiceAmount()))
                    .excessCutoffDate(request.getExcessCutoffDate())
                    .excessStartDate(request.getExcessStartDate())
                    .excessEndDate(request.getExcessEndDate())
                    .excessTotalContributions(safeBigDecimal(request.getExcessTotalContributions()))
                    .excessTotalInterest(safeBigDecimal(request.getExcessTotalInterest()))
                    .excessEolMonths(safeInteger(request.getExcessEolMonths()))
                    .claimApplication(claimApplication)
                    .build();
            claimCalculationSummary.setClaimApplication(claimApplication);
        } else {
            // UPDATE EXISTING
            updateExistingSummary(claimCalculationSummary, request);
            claimCalculationSummary.setClaimApplication(claimApplication);
        }

        // 2. Save the summary first
        claimCalculationSummary = calculationSummaryRepository.saveAndFlush(claimCalculationSummary);
        log.info("Saved calculation summary with ID: {}", claimCalculationSummary.getId());

        // 3. Process rule evaluations - WITH NULL CHECK
        if (request.getRuleEvaluations() != null && !request.getRuleEvaluations().isEmpty()) {
            storeClaimApplicationRuleEvaluation(claimCalculationSummary, request.getRuleEvaluations());
        }

        claimApplication.setCalculationSummary(claimCalculationSummary);        
        return claimCalculationSummary;
    }

    // ==================== UPDATE METHODS ====================

    @Override
    @Transactional
    public ClaimApplicationCalculationSummary patch(long calculationId,
                    ClaimApplicationCalculationSummaryRequest request) {

        log.info("Patching calculation summary with ID: {}", calculationId);

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        ClaimApplicationCalculationSummary claimCalculationSummary = calculationSummaryRepository
                        .findById(calculationId)
                        .orElseThrow(() -> new RuntimeException(
                                        "Calculation summary not found with id: " + calculationId));

        // Update only non-null fields
        updateExistingSummary(claimCalculationSummary, request);
        claimCalculationSummary.setUpdatedBy(safeString(request.getCreatedBy(), "SYSTEM"));

        // If rule evaluations are provided, update them
        if (request.getRuleEvaluations() != null && !request.getRuleEvaluations().isEmpty()) {
            // Clear existing rule evaluations
            if (claimCalculationSummary.getRuleEvaluations() != null) {
                claimCalculationSummary.getRuleEvaluations().clear();
            }
            // Add new rule evaluations
            storeClaimApplicationRuleEvaluation(claimCalculationSummary, request.getRuleEvaluations());
        }

        return calculationSummaryRepository.save(claimCalculationSummary);
    }

    private void updateExistingSummary(ClaimApplicationCalculationSummary summary, 
            ClaimApplicationCalculationSummaryRequest request) {
        
        if (request == null) return;
        
        // Update only non-null fields
        if (request.getCalculationEffectiveDate() != null) {
            summary.setCalculationEffectiveDate(request.getCalculationEffectiveDate());
        }
        if (request.getFinalPayableAmount() != null) {
            summary.setFinalPayableAmount(request.getFinalPayableAmount());
        }
        if (request.getTotalAmount() != null) {
            summary.setTotalAmount(request.getTotalAmount());
        }
        if (request.getIsPfEligible() != null) {
            summary.setIsPfEligible(request.getIsPfEligible());
        }
        if (request.getIsPensionEligible() != null) {
            summary.setIsPensionEligible(request.getIsPensionEligible());
        }
        if (request.getTotalContributionMonth() != null) {
            summary.setTotalContributionMonth(request.getTotalContributionMonth());
        }
        if (request.getTotalNonContributionMonth() != null) {
            summary.setTotalNonContributionMonth(request.getTotalNonContributionMonth());
        }
        if (request.getTotalPfAmount() != null) {
            summary.setTotalPfAmount(request.getTotalPfAmount());
        }
        if (request.getTotalPensionAmount() != null) {
            summary.setTotalPensionAmount(request.getTotalPensionAmount());
        }
        if (request.getTotalPfInterest() != null) {
            summary.setTotalPfInterest(request.getTotalPfInterest());
        }
        if (request.getTotalPensionInterest() != null) {
            summary.setTotalPensionInterest(request.getTotalPensionInterest());
        }
        if (request.getRecommendedBenefitType() != null) {
            summary.setRecommendedBenefitType(request.getRecommendedBenefitType());
        }
        if (request.getExcessOpeningBalance() != null) {
            summary.setExcessOpeningBalance(request.getExcessOpeningBalance());
        }
        if (request.getExcessServiceAmount() != null) {
            summary.setExcessServiceAmount(request.getExcessServiceAmount());
        }
        if (request.getExcessCutoffDate() != null) {
            summary.setExcessCutoffDate(request.getExcessCutoffDate());
        }
        if (request.getExcessStartDate() != null) {
            summary.setExcessStartDate(request.getExcessStartDate());
        }
        if (request.getExcessEndDate() != null) {
            summary.setExcessEndDate(request.getExcessEndDate());
        }
        if (request.getExcessTotalContributions() != null) {
            summary.setExcessTotalContributions(request.getExcessTotalContributions());
        }
        if (request.getExcessTotalInterest() != null) {
            summary.setExcessTotalInterest(request.getExcessTotalInterest());
        }
        if (request.getExcessEolMonths() != null) {
            summary.setExcessEolMonths(request.getExcessEolMonths());
        }
    }

@Transactional
private void storeClaimApplicationRuleEvaluation(
        ClaimApplicationCalculationSummary claimCalculationSummary,
        List<ClaimApplicationRuleEvaluationRequestDto> requests) {

    if (claimCalculationSummary == null || requests == null || requests.isEmpty()) {
        log.warn("Cannot store rule evaluations: summary or requests is null/empty");
        return;
    }

    log.info("Processing {} rule evaluations for summary ID: {}", 
            requests.size(), claimCalculationSummary.getId());

    for (ClaimApplicationRuleEvaluationRequestDto request : requests) {
        if (request == null) {
            log.warn("Skipping null rule evaluation request");
            continue;
        }

        // Get sub rule
        SubClaimMapping subRule = null;
        if (request.getSubRuleCode() != null) {
            subRule = subClaimMappingRepository
                    .findBySubClaimCodeIgnoreCase(request.getSubRuleCode())
                    .orElse(null);
            if (subRule == null) {
                log.warn("SubRule not found for code: {}", request.getSubRuleCode());
            }
        }

        // Get existing or create new
        ClaimApplicationRuleEvaluation ruleEvaluation = null;
        if (request.getRuleEvaluationId() != null && request.getRuleEvaluationId() > 0) {
            ruleEvaluation = claimApplicationRuleEvaluationRepository
                    .findById(request.getRuleEvaluationId())
                    .orElse(null);
        }

        if (ruleEvaluation == null) {
            // CREATE NEW
            // 🔥 FIX: Check for null before calling getIsSpecialCase()
            ActivityEnum isSpecialCase = ActivityEnum.N; // default value
            
            if (claimCalculationSummary.getClaimApplication() != null && 
                claimCalculationSummary.getClaimApplication().getIsSpecialCase() != null) {
                isSpecialCase = claimCalculationSummary.getClaimApplication().getIsSpecialCase();
            }
            
            ActivityEnum isRuleApplied = isSpecialCase.equals(ActivityEnum.Y) ? ActivityEnum.N : ActivityEnum.Y;

            ruleEvaluation = ClaimApplicationRuleEvaluation.builder()
                    .calculationSummary(claimCalculationSummary)
                    .subRule(subRule)
                    .subRuleCode(safeString(request.getSubRuleCode()))
                    .isRuleApplied(isRuleApplied)
                    .resultMessage(safeString(request.getResultMessage()))
                    .remarks(safeString(request.getRemarks()))
                    .evaluatedBy(safeString(request.getEvaluatedBy(), 
                            claimCalculationSummary.getCreatedBy() != null ? 
                            claimCalculationSummary.getCreatedBy() : "SYSTEM"))
                    .evaluatedAt(request.getEvaluatedAt() != null ? 
                            request.getEvaluatedAt() : new Timestamp(System.currentTimeMillis()))
                    .createdBy(safeString(claimCalculationSummary.getCreatedBy(), "SYSTEM"))
                    .build();

            // Build components
            if (request.getComponents() != null && !request.getComponents().isEmpty()) {
                List<ClaimApplicationCalculationComponent> components = 
                        buildComponentsForRuleEvaluation(ruleEvaluation, request.getComponents());
                ruleEvaluation.setComponents(components);
            }
            claimApplicationRuleEvaluationRepository.saveAndFlush(ruleEvaluation);

        } else {
            // UPDATE EXISTING
            ruleEvaluation.setCalculationSummary(claimCalculationSummary);
            ruleEvaluation.setSubRule(subRule);
            ruleEvaluation.setSubRuleCode(safeString(request.getSubRuleCode()));
            
            // 🔥 FIX: Check for null before calling getIsSpecialCase()
            ActivityEnum isSpecialCase = ActivityEnum.N; // default value
            
            if (claimCalculationSummary.getClaimApplication() != null && 
                claimCalculationSummary.getClaimApplication().getIsSpecialCase() != null) {
                isSpecialCase = claimCalculationSummary.getClaimApplication().getIsSpecialCase();
            }
            
            ActivityEnum isRuleApplied = isSpecialCase.equals(ActivityEnum.Y) ? ActivityEnum.N : ActivityEnum.Y;
            ruleEvaluation.setIsRuleApplied(isRuleApplied);
            
            ruleEvaluation.setResultMessage(safeString(request.getResultMessage()));
            ruleEvaluation.setRemarks(safeString(request.getRemarks()));
            ruleEvaluation.setEvaluatedBy(safeString(request.getEvaluatedBy(), 
                    claimCalculationSummary.getCreatedBy() != null ? 
                    claimCalculationSummary.getCreatedBy() : "SYSTEM"));
            ruleEvaluation.setEvaluatedAt(request.getEvaluatedAt() != null ? 
                    request.getEvaluatedAt() : new Timestamp(System.currentTimeMillis()));
            ruleEvaluation.setUpdatedBy(safeString(claimCalculationSummary.getCreatedBy(), "SYSTEM"));

            // Handle components
            if (request.getComponents() != null && !request.getComponents().isEmpty()) {
                List<ClaimApplicationCalculationComponent> newComponents = 
                        buildComponentsForRuleEvaluation(ruleEvaluation, request.getComponents());
                
                // Clear existing and add new
                if (ruleEvaluation.getComponents() != null) {
                    ruleEvaluation.getComponents().clear();
                    ruleEvaluation.getComponents().addAll(newComponents);
                } else {
                    ruleEvaluation.setComponents(newComponents);
                }
            } else {
                // Clear components if none in request
                if (ruleEvaluation.getComponents() != null) {
                    ruleEvaluation.getComponents().clear();
                }
            }

            claimApplicationRuleEvaluationRepository.saveAndFlush(ruleEvaluation);
        }
    }
}
    @Transactional
    private List<ClaimApplicationCalculationComponent> buildComponentsForRuleEvaluation(
            ClaimApplicationRuleEvaluation ruleEvaluation,
            List<ClaimApplicationCalculationComponentRequestDto> componentRequests) {

        if (ruleEvaluation == null || componentRequests == null || componentRequests.isEmpty()) {
            return new ArrayList<>();
        }

        List<ClaimApplicationCalculationComponent> components = new ArrayList<>();

        for (ClaimApplicationCalculationComponentRequestDto componentRequest : componentRequests) {
            if (componentRequest == null) {
                continue;
            }

            // Get component master by code
            ComponentMaster componentMaster = null;
            if (componentRequest.getComponentCode() != null) {
                componentMaster = componentMasterRepository
                        .findByCode(componentRequest.getComponentCode())
                        .orElse(null);
                if (componentMaster == null) {
                    log.warn("Component not found with code: {}", componentRequest.getComponentCode());
                }
            }

            // Check if component already exists (for updates)
            ClaimApplicationCalculationComponent component = null;
            if (componentRequest.getCalculationComponentId() != null && 
                componentRequest.getCalculationComponentId() > 0) {
                component = calculationComponentRepository
                        .findById(componentRequest.getCalculationComponentId())
                        .orElse(null);
            }

            if (component == null) {
                // CREATE NEW
                component = ClaimApplicationCalculationComponent.builder()
                        .componentCode(safeString(componentRequest.getComponentCode()))
                        .componentMaster(componentMaster)
                        .amount(safeBigDecimal(componentRequest.getAmount()))
                        .notes(safeString(componentRequest.getNotes()))
                        .ruleEvaluation(ruleEvaluation)
                        .isDeduction(ActivityEnum.N)
                        .isActive(ActivityEnum.Y)
                        .createdBy(safeString(ruleEvaluation.getCreatedBy(), "SYSTEM"))
                        .build();
            } else {
                // UPDATE EXISTING
                component.setComponentCode(safeString(componentRequest.getComponentCode()));
                component.setComponentMaster(componentMaster);
                component.setAmount(safeBigDecimal(componentRequest.getAmount()));
                component.setNotes(safeString(componentRequest.getNotes()));
                component.setRuleEvaluation(ruleEvaluation);
                component.setUpdatedBy(safeString(ruleEvaluation.getCreatedBy(), "SYSTEM"));
            }

            components.add(component);
        }

        return components;
    }
}