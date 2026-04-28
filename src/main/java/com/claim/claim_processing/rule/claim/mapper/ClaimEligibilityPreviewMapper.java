package com.claim.claim_processing.rule.claim.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;

@Mapper(componentModel = "spring")
public interface ClaimEligibilityPreviewMapper {
    
    ClaimEligibilityPreviewMapper INSTANCE = Mappers.getMapper(ClaimEligibilityPreviewMapper.class);

    @Mapping(target = "eligible", constant = "true")
    @Mapping(source = "contributionSummary", target = "contributionSummary")
    @Mapping(source = "matchingRule.ruleCode", target = "matchingRuleCode")
    @Mapping(source = "matchingRule.ruleName", target = "matchingRuleName")
    @Mapping(source = "componentMappings", target = "eligibleBenefits", qualifiedByName = "toEligibleBenefits")
    @Mapping(target = "ruleEvaluations", ignore = true)
    ClaimEligibilityPreviewResponse toResponse(
        MemberContributionSummary contributionSummary,
        ClaimEligibilityMaster matchingRule,
        List<ClaimEligibilityComponentMap> componentMappings
    );
    
    @Named("toEligibleBenefits")
    default List<EligibleBenefitComponentDTO> toEligibleBenefits(List<ClaimEligibilityComponentMap> componentMappings) {
        if (componentMappings == null) return null;
        
        return componentMappings.stream()
            .map(this::toEligibleBenefitDTO)
            .collect(Collectors.toList());
    }
    
    @Mapping(source = "benefitComponentType.id", target = "benefitComponentTypeId")
    @Mapping(source = "benefitComponentType.code", target = "code")
    @Mapping(source = "benefitComponentType.name", target = "name")
    @Mapping(target = "isEligible", ignore = true)
    @Mapping(target = "selectable", ignore = true)
    @Mapping(target = "ineligibilityReason", ignore = true)
    EligibleBenefitComponentDTO toEligibleBenefitDTO(ClaimEligibilityComponentMap componentMap);
    
    // Post-processing to set default values
    default EligibleBenefitComponentDTO toEligibleBenefitDTOWithDefaults(ClaimEligibilityComponentMap componentMap) {
        EligibleBenefitComponentDTO dto = toEligibleBenefitDTO(componentMap);
        if (dto != null) {
            dto.setIsEligible(com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum.ELIGIBLE);
            dto.setSelectable(true);
            dto.setIneligibilityReason(null);
        }
        return dto;
    }
}