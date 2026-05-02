package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.DecisionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;
import com.claim.claim_processing.common.entities.common.DecisionMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DecisionMapper {

    // -------------------------------
    // CREATE
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DecisionMaster toEntity(DecisionRequestDto dto);

    // -------------------------------
    // RESPONSE
    // -------------------------------
    DecisionResponseDto toResponseDto(DecisionMaster entity);

    List<DecisionResponseDto> toResponseDtoList(List<DecisionMaster> entities);

    // -------------------------------
    // PATCH UPDATE (IMPORTANT)
    // -------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(DecisionRequestDto dto, @MappingTarget DecisionMaster entity);
}