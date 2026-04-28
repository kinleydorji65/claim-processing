package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundCategoryMapResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundCategoryMap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClaimLapsedRefundCategoryMapMapper {

    // =====================================================
    // REQUEST DTO -> ENTITY
    // FK objects will be set in service layer
    // =====================================================
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "category", ignore = true)
    ClaimLapsedRefundCategoryMap toEntity(
            ClaimLapsedRefundCategoryMapRequestDto dto
    );

    // =====================================================
    // ENTITY -> RESPONSE DTO
    // =====================================================
    @Mapping(target = "claimLapsedRefundResponse", source = "rule")
    @Mapping(target = "category", source = "category")
    ClaimLapsedRefundCategoryMapResponseDto toDto(
            ClaimLapsedRefundCategoryMap entity
    );

    // =====================================================
    // UPDATE EXISTING ENTITY
    // FK handled in service layer
    // =====================================================
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(
            ClaimLapsedRefundCategoryMapRequestDto dto,
            @MappingTarget ClaimLapsedRefundCategoryMap entity
    );
}