package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.StageRequestDto;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.entities.common.StageMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StageMapper {

    // 🔹 CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true) // entity default handles if null
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    StageMaster toEntity(StageRequestDto dto);

    // 🔹 ENTITY → RESPONSE
    StageResponseDto toResponseDto(StageMaster entity);

    List<StageResponseDto> toResponseDtoList(List<StageMaster> entities);

    // 🔹 UPDATE (PATCH style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            StageRequestDto dto,
            @MappingTarget StageMaster entity
    );
}