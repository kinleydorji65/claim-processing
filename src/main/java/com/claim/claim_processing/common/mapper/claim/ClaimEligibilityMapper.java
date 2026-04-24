package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.*;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.*;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.claim.*;
import com.claim.claim_processing.common.repository.contribution.SchemeTypeRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ClaimEligibilityMapper {

    @Autowired
    protected ClaimCircumstanceRepository claimCircumstanceRepository;

    @Autowired
    protected CessationTypeRepository cessationTypeRepository;

    @Autowired
    protected SchemeTypeRepository schemeMasterRepository;

    @Autowired
    protected AgencyCategoryRepository agencyCategoryRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract ClaimEligibilityMaster toEntity(ClaimEligibilityCreateRequestDto dto);

    @AfterMapping
    protected void setForeignKeyEntities(
            ClaimEligibilityCreateRequestDto dto,
            @MappingTarget ClaimEligibilityMaster entity
    ) {
        if (dto.getClaimCircumstanceId() != null) {
            ClaimCircumstanceMaster claimCircumstance =
                    claimCircumstanceRepository.findById(dto.getClaimCircumstanceId())
                            .orElseThrow(() -> new RuntimeException("Claim Circumstance not found"));

            entity.setClaimCircumstance(claimCircumstance);
        }

        if (dto.getCessationTypeId() != null) {
            CessationTypeMaster cessationType =
                    cessationTypeRepository.findById(dto.getCessationTypeId())
                            .orElseThrow(() -> new RuntimeException("Cessation Type not found"));

            entity.setCessationType(cessationType);
        }

        if (dto.getSchemeTypeId() != null) {
            SchemeMaster schemeType =
                    schemeMasterRepository.findById(dto.getSchemeTypeId())
                            .orElseThrow(() -> new RuntimeException("Scheme Type not found"));

            entity.setSchemeType(schemeType);
        }

        if (dto.getCategoryId() != null) {
            AgencyCategory category =
                    agencyCategoryRepository.findById(dto.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Agency Category not found"));

            entity.setCategory(category);
        }
    }

    @Mapping(target = "claimCircumstance", source = "claimCircumstance")
    @Mapping(target = "cessationType", source = "cessationType")
    @Mapping(target = "schemeType", source = "schemeType")
    @Mapping(target = "category", source = "category")
    public abstract ClaimEligibilityResponseDto toResponseDto(ClaimEligibilityMaster entity);

    public abstract List<ClaimEligibilityResponseDto> toResponseDtoList(List<ClaimEligibilityMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleCode", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract void updateEntityFromDto(
            ClaimEligibilityUpdateRequestDto dto,
            @MappingTarget ClaimEligibilityMaster entity
    );

    @AfterMapping
    protected void setForeignKeyEntitiesForUpdate(
            ClaimEligibilityUpdateRequestDto dto,
            @MappingTarget ClaimEligibilityMaster entity
    ) {
        if (dto.getClaimCircumstanceId() != null) {
            entity.setClaimCircumstance(
                    claimCircumstanceRepository.findById(dto.getClaimCircumstanceId())
                            .orElseThrow(() -> new RuntimeException("Claim Circumstance not found"))
            );
        }

        if (dto.getCessationTypeId() != null) {
            entity.setCessationType(
                    cessationTypeRepository.findById(dto.getCessationTypeId())
                            .orElseThrow(() -> new RuntimeException("Cessation Type not found"))
            );
        }

        if (dto.getSchemeTypeId() != null) {
            entity.setSchemeType(
                    schemeMasterRepository.findById(dto.getSchemeTypeId())
                            .orElseThrow(() -> new RuntimeException("Scheme Type not found"))
            );
        }

        if (dto.getCategoryId() != null) {
            entity.setCategory(
                    agencyCategoryRepository.findById(dto.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Agency Category not found"))
            );
        }
    }

    protected ClaimCircumstanceResponseDto map(ClaimCircumstanceMaster entity) {
        if (entity == null) return null;

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

    protected CessationTypeResponseDto map(CessationTypeMaster entity) {
        if (entity == null) return null;

        return CessationTypeResponseDto.builder()
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
        if (entity == null) return null;

        return SchemeTypeResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .build();
    }

    protected AgencyCategoryDTO map(AgencyCategory entity) {
        if (entity == null) return null;

        return AgencyCategoryDTO.builder()
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .build();
    }
}