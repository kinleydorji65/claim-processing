package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.VerificationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.VerificationStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.VerificationStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VerificationStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    VerificationStatusResponseDto toResponseDto(VerificationStatusMaster entity);

    List<VerificationStatusResponseDto> toResponseDtoList(
            List<VerificationStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    VerificationStatusMaster toEntity(VerificationStatusRequestDto dto);

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
            @MappingTarget VerificationStatusMaster entity,
            VerificationStatusRequestDto dto
    );
}