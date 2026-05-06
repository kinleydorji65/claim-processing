package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ApprovalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.ApprovalStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.ApprovalStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    ApprovalStatusResponseDto toResponseDto(ApprovalStatusMaster entity);

    List<ApprovalStatusResponseDto> toResponseDtoList(List<ApprovalStatusMaster> entities);

    // =========================
    // Create DTO -> Entity
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ApprovalStatusMaster toEntity(ApprovalStatusRequestDto dto);

    // =========================
    // Update DTO -> Entity
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget ApprovalStatusMaster entity,
                      ApprovalStatusRequestDto dto);
}