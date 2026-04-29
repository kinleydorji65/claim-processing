package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleMasterResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClaimVestingRuleMasterMapper {

    // -----------------------------
    // ENTITY → RESPONSE DTO
    // -----------------------------
    // No need for @Mapping when source and target have the same name
    ClaimVestingRuleMasterResponseDto toResponseDto(ClaimVestingRuleMaster entity);

    // -----------------------------
    // REQUEST DTO → ENTITY
    // -----------------------------
    @Mapping(target = "category", ignore = true) // FK handled in service
    @Mapping(target = "id", ignore = true)       // for create safety
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ClaimVestingRuleMaster toEntity(ClaimVestingRuleMasterRequestDto dto);

    // -----------------------------
    // UPDATE EXISTING ENTITY
    // -----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true) // FK handled in service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy")
    void updateEntityFromDto(
            ClaimVestingRuleMasterRequestDto dto,
            @MappingTarget ClaimVestingRuleMaster entity
    );
}