package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityComponentMapResponseDto;
import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityComponentMapRequestDto;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimEligibilityComponentMapMapper {

    // -------------------------------
    // CREATE
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    ClaimEligibilityComponentMap toEntity(ClaimEligibilityComponentMapRequestDto dto);

    // -------------------------------
    // RESPONSE
    // -------------------------------
    @Mapping(source = "rule.id", target = "ruleId")
    @Mapping(source = "rule.ruleCode", target = "ruleCode")
    @Mapping(source = "rule.ruleName", target = "ruleName")
    @Mapping(source = "benefitComponentType.id", target = "benefitComponentTypeId")
    @Mapping(source = "benefitComponentType.code", target = "benefitComponentTypeCode")
    @Mapping(source = "benefitComponentType.name", target = "benefitComponentTypeName")
    ClaimEligibilityComponentMapResponseDto toResponseDto(
            ClaimEligibilityComponentMap entity
    );

    // -------------------------------
    // LIST RESPONSE
    // -------------------------------
    List<ClaimEligibilityComponentMapResponseDto> toResponseDtoList(
            List<ClaimEligibilityComponentMap> entities
    );

    // -------------------------------
    // UPDATE
    // -------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    void updateEntityFromDto(
            ClaimEligibilityComponentMapRequestDto dto,
            @MappingTarget ClaimEligibilityComponentMap entity
    );
}