package com.claim.claim_processing.rule.ruleGateWay.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import com.claim.claim_processing.common.repository.adjustmentMaster.LoanTypeRepository;
import com.claim.claim_processing.common.repository.common.RuleTypeMasterRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import com.claim.claim_processing.rule.ruleGateWay.dto.PartialWithdrawalAccumulationResponseDto;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto.ClaimRuleResponseDto;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto.ClaimRuleResponseDto.AgencyCategories;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto.ClaimRuleResponseDto.AgencyCategories.Components;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto.ClaimRuleResponseDto.AgencyCategories.RefundTypeDTO;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto.ClaimRuleResponseDto.ClaimRuleConditionResponse;
import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleCategoryMap;
import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleMaster;
import com.claim.claim_processing.rule.ruleGateWay.repositories.ClaimRuleCategoryMapRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.ClaimRuleComponentMapRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.ClaimRuleConditionRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.ClaimRuleMasterRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.ClaimRuleRefundTypeMapRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.PartialWithdrawalReasonRepository;
import com.claim.claim_processing.rule.ruleGateWay.service.RuleGateWayService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleGateWayServiceImpl implements RuleGateWayService {

    private final ClaimRuleMasterRepository claimRuleMasterRepository;
    private final ClaimRuleConditionRepository claimRuleConditionRepository;
    private final ClaimRuleCategoryMapRepository claimRuleCategoryMapRepository;
    private final RuleTypeMasterRepository ruleTypeMasterRepository;
    private final ClaimRuleComponentMapRepository claimRuleComponentMapRepository;
    private final SchemeTypeRepository schemeTypeRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final PartialWithdrawalReasonRepository partialWithdrawalReasonRepository;
    private final ClaimRuleRefundTypeMapRepository claimRuleRefundTypeMapRepository;

    @Override
    @Transactional
    public ApiResponseDTO<RuleResponseDto> getByTopRuleType(Long ruleId) {

        RuleTypeMaster ruleType = ruleTypeMasterRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException(
                        "Rule type not found with id: " + ruleId));

        List<ClaimRuleMaster> claimRules =
                claimRuleMasterRepository.findByRuleTypeId(ruleType.getId());

        List<ClaimRuleResponseDto> subClaimRules = claimRules.stream()
                .map(this::mapClaimRuleToResponse)
                .toList();

        return ApiResponseDTO.success(RuleResponseDto.builder()
                .id(ruleType.getId())
                .code(ruleType.getCode())
                .name(ruleType.getName())
                .ruleEffect(ruleType.getRuleEffect())
                .subClaimRules(subClaimRules)
                .build());
    }

    private ClaimRuleResponseDto mapClaimRuleToResponse(ClaimRuleMaster claimRuleMaster) {

        return ClaimRuleResponseDto.builder()
                .id(claimRuleMaster.getId())
                .ruleCode(claimRuleMaster.getRuleCode())
                .ruleName(claimRuleMaster.getRuleName())
                .ruleTypeId(claimRuleMaster.getRuleTypeId())
                .loanTypeId(claimRuleMaster.getLoanTypeId())
                .loanType(resolveLoanType(claimRuleMaster.getLoanTypeId()))
                .partialReasonId(claimRuleMaster.getPartialReasonId())
                .partialReason(resolvePartialReason(claimRuleMaster.getPartialReasonId()))
                .description(claimRuleMaster.getDescription())
                .stopOnSuccess(claimRuleMaster.getStopOnSuccess())
                .effectiveFrom(claimRuleMaster.getEffectiveFrom())
                .effectiveTo(claimRuleMaster.getEffectiveTo())
                .isActive(claimRuleMaster.getIsActive())
                .createdAt(claimRuleMaster.getCreatedAt())
                .createdBy(claimRuleMaster.getCreatedBy())
                .updatedAt(claimRuleMaster.getUpdatedAt())
                .updatedBy(claimRuleMaster.getUpdatedBy())
                .claimRuleCondition(mapRuleCondition(claimRuleMaster.getId()))
                .build();
    }

    private ClaimRuleConditionResponse mapRuleCondition(Long ruleId) {

        return claimRuleConditionRepository
                .findByRule_Id(ruleId)
                .stream()
                .findFirst()
                .map(condition -> ClaimRuleConditionResponse.builder()
                        .id(condition.getId())
                        .schemeTypeName(getSchemeType(condition.getSchemeTypeId()))
                        .schemeTypeId(condition.getSchemeTypeId())
                        .minMonths(condition.getMinMonths())
                        .maxMonths(condition.getMaxMonths())
                        .withdrawalPercentage(condition.getWithdrawalPercentage())
                        .totalContributionNumber(condition.getMinMonths())
                        .priorityOrder(condition.getPriorityOrder())
                        .comparisonType(condition.getComparisonType())
                        .isActive(condition.getIsActive())
                        .agencyCategories(mapAgencyCategories(ruleId))
                        .accumulation(mapAccumulation(condition.getAccumulation()))
                        .build())
                .orElse(null);
    }

    private List<AgencyCategories> mapAgencyCategories(Long ruleId) {
        return claimRuleCategoryMapRepository
                .findByRule_Id(ruleId)
                .stream()
                .map(this::mapAgencyCategory)
                .toList();
    }

    private AgencyCategories mapAgencyCategory(ClaimRuleCategoryMap categoryMap) {

        AgencyCategory category = categoryMap.getCategory();

        return AgencyCategories.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .components(mapComponents(categoryMap.getId()))
                .refundTypes(mapRefundTypes(categoryMap.getId()))
                .build();
    }

    private List<Components> mapComponents(Long ruleCategoryMapId) {
        return claimRuleComponentMapRepository
                .findByRuleCategoryMap_Id(ruleCategoryMapId)
                .stream()
                .map(componentMap -> {
                    ComponentMaster component = componentMap.getComponent();

                    return Components.builder()
                            .componentId(component.getId())
                            .name(component.getName())
                            .build();
                })
                .toList();
    }

    private List<RefundTypeDTO> mapRefundTypes(Long ruleCategoryMapId) {
        return claimRuleRefundTypeMapRepository
                .findByRuleCategoryMap_Id(ruleCategoryMapId)
                .stream()
                .filter(map -> map.getRefundType() != null)
                .map(map -> RefundTypeDTO.builder()
                        .id(map.getRefundType().getId())
                        .name(map.getRefundType().getName())
                        .build())
                .toList();
    }

    private PartialWithdrawalAccumulationResponseDto mapAccumulation(
        PartialWithdrawalAccumulationMaster accumulation
) {
    if (accumulation == null) {
        return null;
    }

    return PartialWithdrawalAccumulationResponseDto.builder()
            .id(accumulation.getId())
            .code(accumulation.getCode())
            .name(accumulation.getName())
            .build();
}

    private String resolveLoanType(Long loanTypeId) {
        return loanTypeId != null && loanTypeId > 0
                ? getLoanType(loanTypeId)
                : null;
    }

    private String resolvePartialReason(Long partialReasonId) {
        return partialReasonId != null && partialReasonId > 0
                ? getPartialReason(partialReasonId)
                : null;
    }

    private String getSchemeType(Long schemeTypeId) {
        if (schemeTypeId == null) {
            return null;
        }

        return schemeTypeRepository.findById(schemeTypeId)
                .map(schemeType -> schemeType.getName())
                .orElse(null);
    }

    private String getLoanType(Long loanTypeId) {
        if (loanTypeId == null) {
            return null;
        }

        return loanTypeRepository.findById(loanTypeId)
                .map(loanType -> loanType.getName())
                .orElse(null);
    }

    private String getPartialReason(Long partialReasonId) {
        if (partialReasonId == null) {
            return null;
        }

        return partialWithdrawalReasonRepository.findById(partialReasonId)
                .map(reason -> reason.getName())
                .orElse(null);
    }
}