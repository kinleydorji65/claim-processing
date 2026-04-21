package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimCircumstanceMapper {

    // CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "Y")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ClaimCircumstanceMaster toEntity(ClaimCircumstanceCreateRequestDto dto);

    // RESPONSE (for dropdown / list)
    ClaimCircumstanceResponseDto toResponseDto(ClaimCircumstanceMaster entity);

    // LIST RESPONSE
    List<ClaimCircumstanceResponseDto> toResponseDtoList(List<ClaimCircumstanceMaster> entities);

    // UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // important: code is immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(ClaimCircumstanceUpdateRequestDto dto,
                             @MappingTarget ClaimCircumstanceMaster entity);
}