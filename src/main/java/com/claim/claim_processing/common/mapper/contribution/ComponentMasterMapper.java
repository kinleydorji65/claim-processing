package com.claim.claim_processing.common.mapper.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.ComponentRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.ComponentResponseDto;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ComponentMasterMapper {

    /**
     * Entity -> Response DTO
     */
    ComponentResponseDto toResponseDto(ComponentMaster entity);

    /**
     * Entity List -> Response DTO List
     */
    List<ComponentResponseDto> toResponseDto(
            List<ComponentMaster> entities
    );

    /**
     * Request DTO -> Entity
     * Ignore auto-managed fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ComponentMaster toEntity(
            ComponentRequestDto dto
    );

    /**
     * Update existing entity from Request DTO
     * Null values will be ignored.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            ComponentRequestDto dto,
            @MappingTarget ComponentMaster entity
    );
}