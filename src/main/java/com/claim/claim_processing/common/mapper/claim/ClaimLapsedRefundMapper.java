package com.claim.claim_processing.common.mapper.claim;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClaimLapsedRefundMapper {

    // TO ENTITY
    public ClaimLapsedRefundMaster toEntity(ClaimLapsedRefundRequestDto dto) {
        if (dto == null) return null;
        
        return ClaimLapsedRefundMaster.builder()
            .ruleCode(dto.getRuleCode())
            .ruleName(dto.getRuleName())
            .claimCategoryCode(dto.getClaimCategoryCode())
            .minContributionMonths(dto.getMinContributionMonths())
            .maxContributionMonths(dto.getMaxContributionMonths())
            .effectiveFrom(dto.getEffectiveFrom())
            .effectiveTo(dto.getEffectiveTo())
            .remarks(dto.getRemarks())
            .isActive(dto.getIsActive() != null ? dto.getIsActive() : ActivityEnum.Y)
            .build();
    }
    
    // TO RESPONSE DTO
    public ClaimLapsedRefundResponseDto toResponseDto(ClaimLapsedRefundMaster entity) {
        if (entity == null) return null;
        
        return ClaimLapsedRefundResponseDto.builder()
            .id(entity.getId())
            .ruleCode(entity.getRuleCode())
            .ruleName(entity.getRuleName())
            .claimCategoryCode(entity.getClaimCategoryCode())
            .claimCircumstance(mapClaimCircumstance(entity.getClaimCircumstance()))
            .cessationType(mapCessationType(entity.getCessationType()))
            .schemeType(mapSchemeType(entity.getSchemeType()))
            .minContributionMonths(entity.getMinContributionMonths())
            .maxContributionMonths(entity.getMaxContributionMonths())
            .effectiveFrom(entity.getEffectiveFrom())
            .effectiveTo(entity.getEffectiveTo())
            .remarks(entity.getRemarks())
            .isActive(entity.getIsActive())
            .createdBy(entity.getCreatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedBy(entity.getUpdatedBy())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    // TO RESPONSE DTO LIST
    public List<ClaimLapsedRefundResponseDto> toResponseDtoList(List<ClaimLapsedRefundMaster> entities) {
        if (entities == null) return null;
        return entities.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }
    
    // UPDATE ENTITY
    public void updateEntityFromDto(ClaimLapsedRefundRequestDto dto, ClaimLapsedRefundMaster entity) {
        if (dto == null || entity == null) return;
        
        if (dto.getRuleCode() != null) entity.setRuleCode(dto.getRuleCode());
        if (dto.getRuleName() != null) entity.setRuleName(dto.getRuleName());
        if (dto.getClaimCategoryCode() != null) entity.setClaimCategoryCode(dto.getClaimCategoryCode());
        if (dto.getMinContributionMonths() != null) entity.setMinContributionMonths(dto.getMinContributionMonths());
        if (dto.getMaxContributionMonths() != null) entity.setMaxContributionMonths(dto.getMaxContributionMonths());
        if (dto.getEffectiveFrom() != null) entity.setEffectiveFrom(dto.getEffectiveFrom());
        if (dto.getEffectiveTo() != null) entity.setEffectiveTo(dto.getEffectiveTo());
        if (dto.getRemarks() != null) entity.setRemarks(dto.getRemarks());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }
    
    // HELPER METHODS
    private ClaimCircumstanceResponseDto mapClaimCircumstance(ClaimCircumstanceMaster circumstance) {
        if (circumstance == null) return null;
        return ClaimCircumstanceResponseDto.builder()
            .id(circumstance.getId())
            .code(circumstance.getCode())
            .name(circumstance.getName())
            .build();
    }
    
    private CessationTypeResponseDto mapCessationType(CessationTypeMaster cessationType) {
        if (cessationType == null) return null;
        return CessationTypeResponseDto.builder()
            .id(cessationType.getId())
            .code(cessationType.getCode())
            .name(cessationType.getName())
            .build();
    }
    
    private SchemeTypeResponseDto mapSchemeType(SchemeMaster schemeType) {
        if (schemeType == null) return null;
        return SchemeTypeResponseDto.builder()
            .id(schemeType.getId())
            .code(schemeType.getCode())
            .name(schemeType.getName())
            .build();
    }
}