package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.ReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewStatusMapper {

    // 🔹 Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true) // handled by entity default
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ReviewStatusMaster toEntity(ReviewStatusRequestDto dto);

    // 🔹 Entity → Response DTO
    ReviewStatusResponseDto toResponseDto(ReviewStatusMaster entity);

    List<ReviewStatusResponseDto> toResponseDtoList(List<ReviewStatusMaster> entities);

    // 🔹 Update (PATCH style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(ReviewStatusRequestDto dto,
                             @MappingTarget ReviewStatusMaster entity);
}