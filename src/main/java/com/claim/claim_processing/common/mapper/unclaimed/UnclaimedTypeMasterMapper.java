package com.claim.claim_processing.common.mapper.unclaimed;

import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedTypeResponseDto;
import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedTypeRequestDto;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnclaimedTypeMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    UnclaimedTypeResponseDto toResponseDto(UnclaimedTypeMaster entity);

    List<UnclaimedTypeResponseDto> toResponseDtoList(
            List<UnclaimedTypeMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UnclaimedTypeMaster toEntity(UnclaimedTypeRequestDto dto);

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
            @MappingTarget UnclaimedTypeMaster entity,
            UnclaimedTypeRequestDto dto
    );
}