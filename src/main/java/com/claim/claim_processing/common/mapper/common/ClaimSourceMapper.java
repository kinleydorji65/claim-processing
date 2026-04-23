package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimSourceRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimSourceResponseDto;
import com.claim.claim_processing.common.DTO.update.common.ClaimSourceUpdateDto;
import com.claim.claim_processing.common.entities.common.ClaimSourceMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimSourceMapper {

    // 🔹 Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true) // handled by entity default
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ClaimSourceMaster toEntity(ClaimSourceRequestDto dto);

    // 🔹 Entity → Response
    ClaimSourceResponseDto toResponseDto(ClaimSourceMaster entity);

    List<ClaimSourceResponseDto> toResponseDtoList(List<ClaimSourceMaster> entities);

    // 🔹 Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(ClaimSourceUpdateDto dto,
                             @MappingTarget ClaimSourceMaster entity);
}