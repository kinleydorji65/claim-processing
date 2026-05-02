package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeRuleMapResponseDto;
import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeRuleMapRequestDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.mapper.common.RuleTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {
                ClaimTypeMasterMapper.class,
                RuleTypeMapper.class
        }
)
public interface ClaimTypeRuleMapMapper {

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    ClaimTypeRuleMap toEntity(ClaimTypeRuleMapRequestDto dto);

    // Entity -> Response DTO
    @Mapping(target = "claimType", source = "claimType")
    @Mapping(target = "ruleType", source = "ruleType")
    ClaimTypeRuleMapResponseDto toResponseDto(ClaimTypeRuleMap entity);

    // Update existing entity from request DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    void updateEntityFromDto(
            ClaimTypeRuleMapRequestDto dto,
            @MappingTarget ClaimTypeRuleMap entity
    );
}