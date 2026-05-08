package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.RuleEvaluationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.RuleEvaluationStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.RuleEvaluationStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RuleEvaluationStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    RuleEvaluationStatusResponseDto toResponseDto(RuleEvaluationStatusMaster entity);

    List<RuleEvaluationStatusResponseDto> toResponseDtoList(
            List<RuleEvaluationStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RuleEvaluationStatusMaster toEntity(RuleEvaluationStatusRequestDto dto);

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
            @MappingTarget RuleEvaluationStatusMaster entity,
            RuleEvaluationStatusRequestDto dto
    );
}