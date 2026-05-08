package com.claim.claim_processing.common.mapper.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedStatusResponseDto;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnclaimedStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    UnclaimedStatusResponseDto toResponseDto(UnclaimedStatusMaster entity);

    List<UnclaimedStatusResponseDto> toResponseDtoList(
            List<UnclaimedStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UnclaimedStatusMaster toEntity(UnclaimedStatusRequestDto dto);

    // =========================
    // PATCH UPDATE
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget UnclaimedStatusMaster entity,
            UnclaimedStatusRequestDto dto
    );
}