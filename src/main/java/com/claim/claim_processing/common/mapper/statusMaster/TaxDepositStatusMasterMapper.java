package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.TaxDepositStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.TaxDepositStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.TaxDepositStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaxDepositStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    TaxDepositStatusResponseDto toResponseDto(TaxDepositStatusMaster entity);

    List<TaxDepositStatusResponseDto> toResponseDtoList(
            List<TaxDepositStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TaxDepositStatusMaster toEntity(TaxDepositStatusRequestDto dto);

    // =========================
    // PATCH UPDATE (partial update)
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget TaxDepositStatusMaster entity,
            TaxDepositStatusRequestDto dto
    );
}