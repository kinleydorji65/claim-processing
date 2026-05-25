package com.claim.claim_processing.rule.claim.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.integration.contribution.dto.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;

@Mapper(componentModel = "spring")
public interface ClaimEligibilityPreviewMapper {
    
    ClaimEligibilityPreviewMapper INSTANCE = Mappers.getMapper(ClaimEligibilityPreviewMapper.class);

    @Mapping(target = "matchingRuleCode", source = "matchingRule.ruleCode")
    @Mapping(target = "matchingRuleName", source = "matchingRule.ruleName")
    @Mapping(target = "eligibleBenefits", source = "categoryBenefitsList")

    ClaimEligibilityPreviewResponse toResponse(
        List<EligibleBenefitComponentDTO> categoryBenefitsList,
        ClaimEligibilityMaster matchingRule
    );
}