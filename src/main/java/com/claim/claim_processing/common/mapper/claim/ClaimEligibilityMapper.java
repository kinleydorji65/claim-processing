package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.*;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;

import java.util.List;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ClaimEligibilityMapper {

    @Autowired
    protected ClaimCircumstanceRepository claimCircumstanceRepository;

    @Autowired
    protected CessationTypeRepository cessationTypeRepository;

    @Autowired
    protected SchemeTypeRepository schemeMasterRepository;

    @Autowired
    protected RuleTypeRepository ruleTypeRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract ClaimEligibilityMaster toEntity(ClaimEligibilityCreateRequestDto dto);

    @AfterMapping
    protected void setForeignKeyEntities(
            ClaimEligibilityCreateRequestDto dto,
            @MappingTarget ClaimEligibilityMaster entity) {
        if (dto.getClaimCircumstanceId() != null) {
            entity.setClaimCircumstance(
                    claimCircumstanceRepository.findById(dto.getClaimCircumstanceId())
                            .orElse(null));
        }

        if (dto.getSchemeTypeId() != null) {
            entity.setSchemeType(
                    schemeMasterRepository.findById(dto.getSchemeTypeId())
                            .orElse(null));
        }

        if (dto.getRuleTypeId() != null) {
            entity.setRuleType(
                    ruleTypeRepository.findById(dto.getRuleTypeId())
                            .orElse(null));
        }
    }



    @Mapping(target = "claimCircumstance", source = "entity", qualifiedByName = "mapCircumstance")
    @Mapping(target = "schemeType", source = "entity", qualifiedByName = "mapSchemeType")
    @Mapping(target = "ruleType", source = "entity", qualifiedByName = "mapRuleType")
    @Mapping(target = "agencyCategories", source = "agencyCategories")
    @Mapping(target = "benefitComponents", source = "benefitComponents")
    public abstract ClaimEligibilityResponseDto toResponseDto(ClaimEligibilityMaster entity, List<AgencyCategory> agencyCategories, List<BenefitComponentTypeMaster> benefitComponents);

    @Named("mapSchemeType")
    SchemeTypeResponseDto mapSchemeType(ClaimEligibilityMaster entity) {
        if (entity.getSchemeType() == null) {
            return null;
        }
        // Custom mapping logic
        return SchemeTypeResponseDto.builder()
            .id(entity.getSchemeType().getId())
            .code(entity.getSchemeType().getCode())
            .name(entity.getSchemeType().getName())
            .build();
    }
    @Named("mapCircumstance")
    ClaimCircumstanceResponseDto mapCircumstance(ClaimEligibilityMaster entity) {
        if (entity.getClaimCircumstance() == null) {
            return null;
        }
        // Custom mapping logic
        return ClaimCircumstanceResponseDto.builder()
            .id(entity.getClaimCircumstance().getId())
            .code(entity.getClaimCircumstance().getCode())
            .name(entity.getClaimCircumstance().getName())
            .build();
    }

    @Named("mapRuleType")
    RuleTypeResponseDto mapRuleType(ClaimEligibilityMaster entity) {
        if (entity.getRuleType() == null) {
            return null;
        }
        // Custom mapping logic
        return RuleTypeResponseDto.builder()
            .id(entity.getRuleType().getId())
            .code(entity.getRuleType().getCode())
            .name(entity.getRuleType().getName())
            .displayOrder(entity.getRuleType().getDisplayOrder())
            .build();
    }
    public abstract List<ClaimEligibilityResponseDto> toResponseDtoList(List<ClaimEligibilityMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleCode", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract void updateEntityFromDto(
            ClaimEligibilityUpdateRequestDto dto,
            @MappingTarget ClaimEligibilityMaster entity);

    @AfterMapping
    protected void setForeignKeyEntitiesForUpdate(
            ClaimEligibilityUpdateRequestDto dto,
            @MappingTarget ClaimEligibilityMaster entity) {
        if (dto.getClaimCircumstanceId() != null) {
            entity.setClaimCircumstance(
                    claimCircumstanceRepository.findById(dto.getClaimCircumstanceId())
                            .orElseThrow(() -> new RuntimeException("Claim Circumstance not found")));
        }

        if (dto.getSchemeTypeId() != null) {
            entity.setSchemeType(
                    schemeMasterRepository.findById(dto.getSchemeTypeId())
                            .orElseThrow(() -> new RuntimeException("Scheme Type not found")));
        }

    }

    protected ClaimCircumstanceResponseDto map(ClaimCircumstanceMaster entity) {
        if (entity == null)
            return null;

        return ClaimCircumstanceResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    protected SchemeTypeResponseDto map(SchemeMaster entity) {
        if (entity == null)
            return null;
        SchemeTypeResponseDto dto = new SchemeTypeResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}