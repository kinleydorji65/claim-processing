package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundComponentMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundComponentMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundComponentMap;
import com.claim.claim_processing.common.mapper.contribution.BenefitComponentTypeMasterMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {BenefitComponentTypeMasterMapper.class}
)
public interface ClaimLapsedRefundComponentMapMapper {

    // ---------------------------------------------------
    // ENTITY → RESPONSE DTO
    // ---------------------------------------------------
    @Mapping(source = "rule.id", target = "ruleId")
    @Mapping(source = "rule.ruleCode", target = "ruleCode")
    @Mapping(source = "rule.ruleName", target = "ruleName")
    @Mapping(source = "benefitComponentType", target = "benefitComponentType")
    ClaimLapsedRefundComponentMapResponseDto toDto(ClaimLapsedRefundComponentMap entity);

    // ---------------------------------------------------
    // REQUEST DTO → ENTITY
    // ---------------------------------------------------
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimLapsedRefundComponentMap toEntity(ClaimLapsedRefundComponentMapRequestDto dto);

    // ---------------------------------------------------
    // UPDATE SUPPORT
    // ---------------------------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            ClaimLapsedRefundComponentMapRequestDto dto,
            @MappingTarget ClaimLapsedRefundComponentMap entity
    );
}