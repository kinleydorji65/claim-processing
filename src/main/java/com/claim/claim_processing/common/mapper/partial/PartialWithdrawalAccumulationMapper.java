package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PartialWithdrawalAccumulationMapper {

    // =========================
    // ENTITY → RESPONSE DTO
    // =========================
    PartialWithdrawalAccumulationResponseDto toDto(
            PartialWithdrawalAccumulationMaster entity);

    // =========================
    // REQUEST DTO → ENTITY
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PartialWithdrawalAccumulationMaster toEntity(
            PartialWithdrawalAccumulationRequestDto dto);

    // =========================
    // UPDATE ENTITY (PARTIAL UPDATE)
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget PartialWithdrawalAccumulationMaster entity,
                      PartialWithdrawalAccumulationRequestDto dto);
}