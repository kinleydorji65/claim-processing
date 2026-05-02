package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimTypeDeductionMapRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimTypeDeductionMapResponseDto;
import com.claim.claim_processing.common.entities.common.ClaimTypeDeductionMap;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClaimTypeDeductionMapMapper {

    // ---------------------------------------------------
    // ENTITY -> RESPONSE DTO
    // ---------------------------------------------------

    @Mapping(source = "claimType", target = "claimType")
    @Mapping(source = "deductionType", target = "deductionType")
    ClaimTypeDeductionMapResponseDto toDto(ClaimTypeDeductionMap entity);

    // ---------------------------------------------------
    // REQUEST DTO -> ENTITY
    // (FK will be set in service layer)
    // ---------------------------------------------------

    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "deductionType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimTypeDeductionMap toEntity(ClaimTypeDeductionMapRequestDto dto);

    // ---------------------------------------------------
    // UPDATE (for PATCH style)
    // ---------------------------------------------------

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "deductionType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(ClaimTypeDeductionMapRequestDto dto,
                             @MappingTarget ClaimTypeDeductionMap entity);
}