package com.claim.claim_processing.rule.claim.mapper;

import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundComponentMap;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LapsedRefundPreviewMapper {
    
    LapsedRefundPreviewMapper INSTANCE = Mappers.getMapper(LapsedRefundPreviewMapper.class);

    @Mapping(target = "eligible", constant = "true")
    @Mapping(source = "matchingRule.ruleCode", target = "matchingRuleCode")
    @Mapping(source = "matchingRule.ruleName", target = "matchingRuleName")
    @Mapping(source = "contributionSummary", target = "contributionSummary")
    @Mapping(source = "componentMappings", target = "lapsedBenefits", qualifiedByName = "mapToLapsedBenefits")
    @Mapping(source = "matchingRule.remarks", target = "remarks")
    LapsedRefundPreviewResponseDTO toResponse(
            MemberContributionSummary contributionSummary,
            ClaimLapsedRefundMaster matchingRule,
            List<ClaimLapsedRefundComponentMap> componentMappings
    );

    @Named("mapToLapsedBenefits")
    default List<EligibleBenefitComponentDTO> mapToLapsedBenefits(List<ClaimLapsedRefundComponentMap> componentMappings) {
        if (componentMappings == null) return null;
        
        return componentMappings.stream()
                .map(this::toEligibleBenefitDTO)
                .collect(Collectors.toList());
    }

    @Mapping(source = "benefitComponentType.id", target = "benefitComponentTypeId")
    @Mapping(source = "benefitComponentType.code", target = "code")
    @Mapping(source = "benefitComponentType.name", target = "name")
    @Mapping(target = "isEligible", expression = "java(com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum.ELIGIBLE)")
    @Mapping(target = "selectable", constant = "true")
    @Mapping(target = "ineligibilityReason", constant = "null")
    EligibleBenefitComponentDTO toEligibleBenefitDTO(ClaimLapsedRefundComponentMap component);
}
