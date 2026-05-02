package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.WorkflowReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.common.WorkflowReasonResponseDto;
import com.claim.claim_processing.common.entities.common.WorkflowReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkflowReasonMapper {

    // 🔹 CREATE mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "displayOrder", ignore = true) // handled by entity default if null
    @Mapping(target = "isActive", ignore = true)     // handled by entity default
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    WorkflowReasonMaster toEntity(WorkflowReasonRequestDto dto);

    // 🔹 ENTITY → RESPONSE
    WorkflowReasonResponseDto toResponseDto(WorkflowReasonMaster entity);

    List<WorkflowReasonResponseDto> toResponseDtoList(List<WorkflowReasonMaster> entities);

    // 🔹 UPDATE (PATCH style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable field
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            WorkflowReasonRequestDto dto,
            @MappingTarget WorkflowReasonMaster entity
    );
}