package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PartialWithdrawalCauseMapper {

    // =========================
    // ENTITY → RESPONSE DTO
    // =========================
    @Mapping(source = "reason", target = "reason")
    PartialWithdrawalCauseResponseDto toDto(PartialWithdrawalCauseMaster entity);

    // =========================
    // REQUEST DTO → ENTITY
    // FK (reason) handled in service layer
    // =========================
    @Mapping(target = "reason", ignore = true)
    PartialWithdrawalCauseMaster toEntity(PartialWithdrawalCauseRequestDto dto);

    // =========================
    // UPDATE ENTITY
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "reason", ignore = true)
    void updateEntity(@MappingTarget PartialWithdrawalCauseMaster entity,
                      PartialWithdrawalCauseRequestDto dto);

    // =========================
    // NESTED MAPPING (Reason → DTO)
    // =========================
    default PartialWithdrawalReasonResponseDto map(PartialWithdrawalReasonMaster reason) {
        if (reason == null) return null;

        return PartialWithdrawalReasonResponseDto.builder()
                .id(reason.getId())
                .code(reason.getCode())
                .name(reason.getName())
                .build();
    }
}