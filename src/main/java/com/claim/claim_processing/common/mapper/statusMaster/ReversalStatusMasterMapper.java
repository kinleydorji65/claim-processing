package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ReversalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.ReversalStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.ReversalStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReversalStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    ReversalStatusResponseDto toResponseDto(ReversalStatusMaster entity);

    List<ReversalStatusResponseDto> toResponseDtoList(
            List<ReversalStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ReversalStatusMaster toEntity(ReversalStatusRequestDto dto);

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
            @MappingTarget ReversalStatusMaster entity,
            ReversalStatusRequestDto dto
    );
}