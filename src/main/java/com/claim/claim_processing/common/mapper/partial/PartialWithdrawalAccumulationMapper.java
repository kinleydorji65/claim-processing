package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartialWithdrawalAccumulationMapper {

    // =========================
    // CREATE
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PartialWithdrawalAccumulationMaster toEntity(
            PartialWithdrawalAccumulationRequestDto dto
    );

    // =========================
    // RESPONSE
    // =========================
    PartialWithdrawalAccumulationResponseDto toResponseDto(
            PartialWithdrawalAccumulationMaster entity
    );

    // =========================
    // LIST RESPONSE
    // =========================
    List<PartialWithdrawalAccumulationResponseDto> toResponseDtoList(
            List<PartialWithdrawalAccumulationMaster> entities
    );

    // =========================
    // UPDATE
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            PartialWithdrawalAccumulationRequestDto dto,
            @MappingTarget PartialWithdrawalAccumulationMaster entity
    );
}