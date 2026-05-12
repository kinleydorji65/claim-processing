package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ActionResponseDto;
import com.claim.claim_processing.common.entities.common.ActionMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ActionMasterMapper {

    // -----------------------------
    // ENTITY → DTO
    // -----------------------------
    ActionResponseDto toResponseDto(ActionMaster entity);

    List<ActionResponseDto> toResponseDto(List<ActionMaster> entities);

    // -----------------------------
    // DTO → ENTITY
    // -----------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ActionMaster toEntity(ActionRequestDto dto);

    // -----------------------------
    // PATCH UPDATE
    // -----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ActionRequestDto dto, @MappingTarget ActionMaster entity);
}