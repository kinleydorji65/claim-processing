package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ActionResponseDto;
import com.claim.claim_processing.common.entities.common.ActionMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActionMasterMapper {

    // -------------------------------
    // ENTITY → RESPONSE DTO
    // -------------------------------
    ActionResponseDto toDto(ActionMaster entity);

    // -------------------------------
    // REQUEST DTO → ENTITY (CREATE)
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ActionMaster toEntity(ActionRequestDto dto);

    // -------------------------------
    // PATCH UPDATE (NULL SAFE)
    // -------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntityFromDto(ActionRequestDto dto,
                            @MappingTarget ActionMaster entity);
}