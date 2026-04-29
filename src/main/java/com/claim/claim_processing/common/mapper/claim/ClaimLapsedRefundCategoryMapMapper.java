package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundCategoryMapResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClaimLapsedRefundCategoryMapMapper {
    
    ClaimLapsedRefundCategoryMapMapper INSTANCE = Mappers.getMapper(ClaimLapsedRefundCategoryMapMapper.class);

    // =====================================================
    // REQUEST DTO -> ENTITY
    // FK objects will be set in service layer
    // =====================================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "category", ignore = true)
    ClaimLapsedRefundCategoryMap toEntity(ClaimLapsedRefundCategoryMapRequestDto dto);

    // =====================================================
    // ENTITY -> RESPONSE DTO
    // =====================================================
    @Mapping(source = "rule", target = "claimLapsedRefundResponse")
    @Mapping(source = "category", target = "category")
    ClaimLapsedRefundCategoryMapResponseDto toDto(ClaimLapsedRefundCategoryMap entity);

    // =====================================================
    // UPDATE EXISTING ENTITY
    // FK handled in service layer
    // =====================================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(ClaimLapsedRefundCategoryMapRequestDto dto, @MappingTarget ClaimLapsedRefundCategoryMap entity);
    
    // =====================================================
    // Helper mapping methods (if needed)
    // =====================================================
    default ClaimLapsedRefundResponseDto mapRuleToResponse(ClaimLapsedRefundMaster rule) {
        if (rule == null) return null;
        return ClaimLapsedRefundResponseDto.builder()
            .id(rule.getId())
            .ruleCode(rule.getRuleCode())
            .ruleName(rule.getRuleName())
            // Add other fields as needed
            .build();
    }
    
    default AgencyCategoryDTO mapCategoryToDTO(AgencyCategory category) {
        if (category == null) return null;
        return AgencyCategoryDTO.builder()
            .categoryId(category.getCategoryId())
            .categoryName(category.getCategoryName())
            // Add other fields as needed
            .build();
    }
}