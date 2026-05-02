package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.InterestMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.common.InterestMethodResponseDto;
import com.claim.claim_processing.common.entities.common.InterestMethodMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InterestMethodMapper {

    // 🔹 Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true) // handled by entity default
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    InterestMethodMaster toEntity(InterestMethodRequestDto dto);

    // 🔹 Entity → Response
    InterestMethodResponseDto toResponseDto(InterestMethodMaster entity);

    List<InterestMethodResponseDto> toResponseDtoList(List<InterestMethodMaster> entities);

    // 🔹 Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(InterestMethodRequestDto dto,
                             @MappingTarget InterestMethodMaster entity);
}