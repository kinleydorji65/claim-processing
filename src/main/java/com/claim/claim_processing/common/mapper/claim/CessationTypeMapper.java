package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CessationTypeMapper {

    // =========================
    // CREATE
    // =========================
    @Mapping(target = "id", ignore = true)

    // FK handled in service
    @Mapping(target = "claimCircumstance", ignore = true)

    // system fields
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    CessationTypeMaster toEntity(CessationTypeCreateRequestDto dto);

    // =========================
    // ENTITY → RESPONSE
    // =========================
    @Mapping(source = "claimCircumstance", target = "claimCircumstance")
    CessationTypeResponseDto toResponseDto(CessationTypeMaster entity);

    List<CessationTypeResponseDto> toResponseDtoList(List<CessationTypeMaster> entities);

    // =========================
    // UPDATE (PATCH)
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)

    // FK handled in service
    @Mapping(target = "claimCircumstance", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDto(
            CessationTypeUpdateRequestDto dto,
            @MappingTarget CessationTypeMaster entity
    );
}