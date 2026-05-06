package com.claim.claim_processing.rule.claim.mapper;

import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LapsedRefundPreviewMapper {
    
    LapsedRefundPreviewMapper INSTANCE = Mappers.getMapper(LapsedRefundPreviewMapper.class);

    @Mapping(target = "matchingRuleCode", source = "matchingRule.ruleCode")
    @Mapping(target = "matchingRuleName", source = "matchingRule.ruleName")
    @Mapping(target = "lapsedBenefits", source = "categoryBenefitsList")
    LapsedRefundPreviewResponseDTO toResponse(
            List<EligibleBenefitComponentDTO> categoryBenefitsList,
            ClaimLapsedRefundMaster matchingRule
    );
}
