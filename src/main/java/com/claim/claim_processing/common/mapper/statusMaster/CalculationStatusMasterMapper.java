package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.CalculationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.CalculationStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.CalculationStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CalculationStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    CalculationStatusResponseDto toResponseDto(CalculationStatusMaster entity);

    List<CalculationStatusResponseDto> toResponseDtoList(List<CalculationStatusMaster> entities);

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    CalculationStatusMaster toEntity(CalculationStatusRequestDto dto);

    // =========================
    // Update Existing Entity
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget CalculationStatusMaster entity,
                      CalculationStatusRequestDto dto);
}