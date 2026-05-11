package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimCircumstanceRepository;
import com.claim.claim_processing.common.repository.common.RuleTypeRepository;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ClaimLapsedRefundMapper {

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
    @Mapping(target = "ruleType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "isActive", source = "isActive", defaultExpression = "java(com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum.Y)")
    public abstract ClaimLapsedRefundMaster toEntity(ClaimLapsedRefundRequestDto dto);


    @AfterMapping
    protected void setForeignKeyEntities(
            ClaimLapsedRefundRequestDto dto,
            @MappingTarget ClaimLapsedRefundMaster entity) {
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
    
    // =============================================
    // TO RESPONSE DTO
    // =============================================
    @Mapping(target = "claimCircumstance", source = "entity", qualifiedByName = "mapClaimCircumstance")
    @Mapping(target = "schemeType", source = "entity", qualifiedByName = "mapSchemeType")
    @Mapping(target = "ruleType", source = "entity", qualifiedByName = "mapRuleType")
    @Mapping(target = "agencyCategories", source = "agencyCategories")
    @Mapping(target = "benefitComponents", source = "benefitComponents")
    public abstract ClaimLapsedRefundResponseDto toResponseDto(ClaimLapsedRefundMaster entity, List<AgencyCategory> agencyCategories, List<BenefitComponentTypeMaster> benefitComponents);

    @Named("mapSchemeType")
    SchemeTypeResponseDto mapSchemeType(ClaimLapsedRefundMaster entity) {
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
    @Named("mapClaimCircumstance")
    ClaimCircumstanceResponseDto mapClaimCircumstance(ClaimLapsedRefundMaster entity) {
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
    RuleTypeResponseDto mapRuleType(ClaimLapsedRefundMaster entity) {
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

    public abstract List<ClaimLapsedRefundResponseDto> toResponseDtoList(List<ClaimLapsedRefundMaster> entities);

    // =============================================
    // TO ENTITY (CREATE)
    // =============================================
    
    // =============================================
    // UPDATE EXISTING ENTITY
    // =============================================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "ruleType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy")
    public abstract void updateEntityFromDto(ClaimLapsedRefundRequestDto dto, @MappingTarget ClaimLapsedRefundMaster entity);

    // =============================================
    // NAMED MAPPERS FOR NESTED OBJECTS
    // =============================================
    @Named("mapClaimCircumstance")
    static ClaimCircumstanceResponseDto mapClaimCircumstance(ClaimCircumstanceMaster circumstance) {
        if (circumstance == null) return null;
        return ClaimCircumstanceResponseDto.builder()
            .id(circumstance.getId())
            .code(circumstance.getCode())
            .name(circumstance.getName())
            .build();
    }

    @Named("mapSchemeType")
    static SchemeTypeResponseDto mapSchemeType(SchemeMaster schemeType) {
        if (schemeType == null) return null;
        return SchemeTypeResponseDto.builder()
            .id(schemeType.getId())
            .code(schemeType.getCode())
            .name(schemeType.getName())
            .build();
    }

    @Named("mapRuleType")
    static RuleTypeResponseDto mapRuleType(RuleTypeMaster ruleType) {
        if (ruleType == null) return null;
        return RuleTypeResponseDto.builder()
                .id(ruleType.getId())
                .code(ruleType.getCode())
                .name(ruleType.getName())
                .build();
    }
}