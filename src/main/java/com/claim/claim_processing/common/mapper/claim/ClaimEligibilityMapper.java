package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import com.claim.claim_processing.common.entities.claim.*;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClaimEligibilityMapper {

    // ---------------------------
    // CREATE DTO -> ENTITY
    // ---------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", source = "claimCircumstanceId")
    @Mapping(target = "cessationType", source = "cessationTypeId")
    @Mapping(target = "schemeType", source = "schemeTypeId")
    @Mapping(target = "isActive", constant = "Y")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimEligibilityMaster toEntity(ClaimEligibilityCreateRequestDto dto);

    // ---------------------------
    // UPDATE DTO -> ENTITY (PATCH STYLE)
    // ---------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "claimCircumstance", source = "claimCircumstanceId")
    @Mapping(target = "cessationType", source = "cessationTypeId")
    @Mapping(target = "schemeType", source = "schemeTypeId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget ClaimEligibilityMaster entity,
                      ClaimEligibilityUpdateRequestDto dto);

    // ---------------------------
    // ENTITY -> RESPONSE DTO
    // ---------------------------
    @Mapping(target = "claimCircumstance", source = "claimCircumstance")
    @Mapping(target = "cessationType", source = "cessationType")
    @Mapping(target = "schemeType", source = "schemeType")
    @Mapping(target = "category", ignore = true) // removed from entity
    ClaimEligibilityResponseDto toResponseDto(ClaimEligibilityMaster entity);

    // =========================================================
    // ID → ENTITY MAPPINGS (IMPORTANT FOR JPA RELATIONS)
    // =========================================================

    default ClaimCircumstanceMaster mapClaimCircumstance(Long id) {
        if (id == null) return null;
        ClaimCircumstanceMaster obj = new ClaimCircumstanceMaster();
        obj.setId(id);
        return obj;
    }

    default CessationTypeMaster mapCessationType(Long id) {
        if (id == null) return null;
        CessationTypeMaster obj = new CessationTypeMaster();
        obj.setId(id);
        return obj;
    }

    default SchemeMaster mapSchemeType(Long id) {
        if (id == null) return null;
        SchemeMaster obj = new SchemeMaster();
        obj.setId(id);
        return obj;
    }

    // =========================================================
    // ENTITY → DTO nested mappings (light mapping)
    // =========================================================

    default ClaimCircumstanceResponseDto map(ClaimCircumstanceMaster entity) {
        if (entity == null) return null;
        ClaimCircumstanceResponseDto dto = new ClaimCircumstanceResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    default CessationTypeResponseDto map(CessationTypeMaster entity) {
        if (entity == null) return null;
        CessationTypeResponseDto dto = new CessationTypeResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    default SchemeTypeResponseDto map(SchemeMaster entity) {
        if (entity == null) return null;
        SchemeTypeResponseDto dto = new SchemeTypeResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}