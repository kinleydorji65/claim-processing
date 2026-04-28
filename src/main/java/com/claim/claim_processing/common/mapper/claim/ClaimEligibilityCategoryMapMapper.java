package com.claim.claim_processing.common.mapper.claim;
import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityCategoryMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityCategoryMap;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimEligibilityCategoryMapMapper {

    // -------------------------
    // ENTITY -> RESPONSE DTO
    // -------------------------
    @Mapping(source = "rule", target = "rule")
    @Mapping(source = "category", target = "category")
    ClaimEligibilityCategoryMapResponseDto toResponseDto(ClaimEligibilityCategoryMap entity);

    List<ClaimEligibilityCategoryMapResponseDto> toResponseDtoList(List<ClaimEligibilityCategoryMap> entities);

    // -------------------------
    // REQUEST DTO -> ENTITY
    // (FK handled in service)
    // -------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "category", ignore = true)
    ClaimEligibilityCategoryMap toEntity(ClaimEligibilityCategoryMapRequestDto dto);

    // -------------------------
    // UPDATE SUPPORT (optional)
    // -------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(
            ClaimEligibilityCategoryMapRequestDto dto,
            @MappingTarget ClaimEligibilityCategoryMap entity
    );
}