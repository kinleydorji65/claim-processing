package com.claim.claim_processing.common.mapper.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationTriggerTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationTriggerTypeResponseDto;
import com.claim.claim_processing.common.entities.calculationMaster.CalculationTriggerTypeMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CalculationTriggerTypeMapper {

    // -------------------------------
    // ENTITY -> RESPONSE DTO
    // -------------------------------
    CalculationTriggerTypeResponseDto toDto(CalculationTriggerTypeMaster entity);

    // -------------------------------
    // CREATE DTO -> ENTITY
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CalculationTriggerTypeMaster toEntity(CalculationTriggerTypeRequestDto dto);

    // -------------------------------
    // PUT UPDATE (FULL REPLACE)
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDto(CalculationTriggerTypeRequestDto dto,
                             @MappingTarget CalculationTriggerTypeMaster entity);

    // -------------------------------
    // PATCH UPDATE (NULL SAFE)
    // -------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void patchEntityFromDto(CalculationTriggerTypeRequestDto dto,
                            @MappingTarget CalculationTriggerTypeMaster entity);
}