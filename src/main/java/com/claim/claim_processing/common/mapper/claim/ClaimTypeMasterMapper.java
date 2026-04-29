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
    @Mapping(source = "isActive", target = "isActive")
    ClaimTypeMasterResponseDto toResponseDto(ClaimTypeMaster entity);

    List<ClaimTypeMasterResponseDto> toResponseDtoList(List<ClaimTypeMaster> entities);

    // -----------------------------
    // REQUEST DTO -> ENTITY (CREATE)
    // -----------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(source = "isActive", target = "isActive")
    ClaimTypeMaster toEntity(ClaimTypeMasterRequestDto dto);

    // -----------------------------
    // REQUEST DTO -> ENTITY (UPDATE)
    // -----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(source = "isActive", target = "isActive")
    void updateEntityFromDto(ClaimTypeMasterRequestDto dto, @MappingTarget ClaimTypeMaster entity);
}