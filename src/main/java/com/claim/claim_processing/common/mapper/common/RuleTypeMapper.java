package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.RuleTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RuleTypeMapper {

    // 🔹 CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "displayOrder", ignore = true) // entity default handles if null
    @Mapping(target = "isActive", ignore = true)     // entity default handles if null
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RuleTypeMaster toEntity(RuleTypeRequestDto dto);

    // 🔹 ENTITY → RESPONSE
    RuleTypeResponseDto toResponseDto(RuleTypeMaster entity);

    List<RuleTypeResponseDto> toResponseDtoList(List<RuleTypeMaster> entities);

    // 🔹 UPDATE (PATCH style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            RuleTypeRequestDto dto,
            @MappingTarget RuleTypeMaster entity
    );
}