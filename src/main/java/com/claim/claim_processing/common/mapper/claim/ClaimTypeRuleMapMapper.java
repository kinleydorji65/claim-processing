package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeRuleMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.mapper.common.RuleTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                ClaimTypeMasterMapper.class,
                RuleTypeMapper.class
        }
)
public interface ClaimTypeRuleMapMapper {

    // Entity -> Response DTO
    @Mapping(target = "claimType", source = "claimType")
    @Mapping(target = "ruleType", source = "ruleType")
    ClaimTypeRuleMapResponseDto toResponseDto(ClaimTypeRuleMap entity);
}