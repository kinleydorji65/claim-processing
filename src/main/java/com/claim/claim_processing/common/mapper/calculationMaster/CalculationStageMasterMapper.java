package com.claim.claim_processing.common.mapper.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationStageRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationStageResponseDto;
import com.claim.claim_processing.common.entities.calculationMaster.CalculationStageMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CalculationStageMasterMapper {

    // Entity -> Response DTO
    CalculationStageResponseDto toDto(CalculationStageMaster entity);

    // Request DTO -> Entity (Create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CalculationStageMaster toEntity(CalculationStageRequestDto dto);

    // Update existing entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(
            CalculationStageRequestDto dto,
            @MappingTarget CalculationStageMaster entity
    );
}