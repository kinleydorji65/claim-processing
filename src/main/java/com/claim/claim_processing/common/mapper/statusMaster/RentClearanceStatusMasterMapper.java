package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RentClearanceStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.RentClearanceStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.RentClearanceStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RentClearanceStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    RentClearanceStatusResponseDto toResponseDto(RentClearanceStatusMaster entity);

    List<RentClearanceStatusResponseDto> toResponseDtoList(
            List<RentClearanceStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RentClearanceStatusMaster toEntity(RentClearanceStatusRequestDto dto);

    // =========================
    // PATCH UPDATE (Partial Update)
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget RentClearanceStatusMaster entity,
            RentClearanceStatusRequestDto dto
    );
}