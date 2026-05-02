package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionTypeResponseDto;
import com.claim.claim_processing.common.entities.common.DeductionTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeductionTypeMapper {

    // -------------------------------
    // CREATE
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true) // handled by entity default or service
    DeductionTypeMaster toEntity(DeductionTypeRequestDto dto);

    // -------------------------------
    // ENTITY → RESPONSE
    // -------------------------------
    DeductionTypeResponseDto toResponseDto(DeductionTypeMaster entity);

    List<DeductionTypeResponseDto> toResponseDtoList(List<DeductionTypeMaster> entities);

    // -------------------------------
    // UPDATE (PATCH STYLE)
    // -------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable business key
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(DeductionTypeRequestDto dto,
                             @MappingTarget DeductionTypeMaster entity);
}