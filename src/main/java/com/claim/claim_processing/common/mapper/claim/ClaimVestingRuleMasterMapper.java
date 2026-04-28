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
    @Mapping(source = "category", target = "category")
    ClaimVestingRuleMasterResponseDto toResponseDto(ClaimVestingRuleMaster entity);

    // -----------------------------
    // REQUEST DTO → ENTITY
    // -----------------------------
    @Mapping(target = "category", ignore = true) // FK handled in service
    @Mapping(target = "id", ignore = true)       // for create safety
    ClaimVestingRuleMaster toEntity(ClaimVestingRuleMasterRequestDto dto);

    // -----------------------------
    // UPDATE EXISTING ENTITY
    // -----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true) // FK handled in service
    void updateEntityFromDto(
            ClaimVestingRuleMasterRequestDto dto,
            @MappingTarget ClaimVestingRuleMaster entity
    );
}