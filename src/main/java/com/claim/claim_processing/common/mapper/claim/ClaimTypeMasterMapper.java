package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimTypeMasterMapper {

    // -----------------------------
    // ENTITY -> RESPONSE DTO
    // -----------------------------
    ClaimTypeMasterResponseDto toResponseDto(ClaimTypeMaster entity);

    List<ClaimTypeMasterResponseDto> toResponseDtoList(List<ClaimTypeMaster> entities);

    // -----------------------------
    // REQUEST DTO -> ENTITY (CREATE)
    // -----------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimTypeMaster toEntity(ClaimTypeMasterRequestDto dto);

    // -----------------------------
    // REQUEST DTO -> ENTITY (UPDATE)
    // -----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(ClaimTypeMasterRequestDto dto, @MappingTarget ClaimTypeMaster entity);
}