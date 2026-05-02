package com.claim.claim_processing.common.mapper.specialCase;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseRefundReasonResponseDto;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialCaseRefundReasonMasterMapper {

    // -------------------------
    // ENTITY → RESPONSE DTO
    // -------------------------
    SpecialCaseRefundReasonResponseDto toResponseDto(
            SpecialCaseRefundReasonMaster entity
    );

    List<SpecialCaseRefundReasonResponseDto> toResponseDtoList(
            List<SpecialCaseRefundReasonMaster> entities
    );

    // -------------------------
    // CREATE (DTO → ENTITY)
    // -------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SpecialCaseRefundReasonMaster toEntity(
            SpecialCaseRefundReasonRequestDto dto
    );

    // -------------------------
    // FULL UPDATE (PUT)
    // -------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            SpecialCaseRefundReasonRequestDto dto,
            @MappingTarget SpecialCaseRefundReasonMaster entity
    );

    // -------------------------
    // PARTIAL UPDATE (PATCH)
    // -------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntityFromDto(
            SpecialCaseRefundReasonRequestDto dto,
            @MappingTarget SpecialCaseRefundReasonMaster entity
    );
}