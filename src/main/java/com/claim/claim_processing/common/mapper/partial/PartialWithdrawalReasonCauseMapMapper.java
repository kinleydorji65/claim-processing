package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonCauseMapRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonCauseMapResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonCauseMap;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartialWithdrawalReasonCauseMapMapper {

    // -----------------------
    // ENTITY -> RESPONSE DTO
    // -----------------------

    @Mapping(source = "reason", target = "reason")
    @Mapping(source = "cause", target = "cause")
    PartialWithdrawalReasonCauseMapResponseDto toDto(PartialWithdrawalReasonCauseMap entity);

    List<PartialWithdrawalReasonCauseMapResponseDto> toDtoList(List<PartialWithdrawalReasonCauseMap> entities);

    // -----------------------
    // REQUEST DTO -> ENTITY (FK SAFE)
    // -----------------------

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "cause", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PartialWithdrawalReasonCauseMap toEntity(PartialWithdrawalReasonCauseMapRequestDto dto);

    // -----------------------
    // UPDATE SUPPORT (PATCH STYLE)
    // -----------------------

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "cause", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PartialWithdrawalReasonCauseMapRequestDto dto,
                      @MappingTarget PartialWithdrawalReasonCauseMap entity);
}