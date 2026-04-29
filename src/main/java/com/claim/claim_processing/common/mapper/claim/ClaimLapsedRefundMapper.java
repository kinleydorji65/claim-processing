package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimCircumstanceMaster;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimLapsedRefundMapper {

    // =============================================
    // TO RESPONSE DTO
    // =============================================
    @Mapping(source = "claimCircumstance", target = "claimCircumstance", qualifiedByName = "mapClaimCircumstance")
    @Mapping(source = "schemeType", target = "schemeType", qualifiedByName = "mapSchemeType")
    ClaimLapsedRefundResponseDto toResponseDto(ClaimLapsedRefundMaster entity);

    List<ClaimLapsedRefundResponseDto> toResponseDtoList(List<ClaimLapsedRefundMaster> entities);

    // =============================================
    // TO ENTITY (CREATE)
    // =============================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "isActive", source = "isActive", defaultExpression = "java(com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum.Y)")
    ClaimLapsedRefundMaster toEntity(ClaimLapsedRefundRequestDto dto);

    // =============================================
    // UPDATE EXISTING ENTITY
    // =============================================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy")
    void updateEntityFromDto(ClaimLapsedRefundRequestDto dto, @MappingTarget ClaimLapsedRefundMaster entity);

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

    @Named("mapCessationType")
    static CessationTypeResponseDto mapCessationType(CessationTypeMaster cessationType) {
        if (cessationType == null) return null;
        return CessationTypeResponseDto.builder()
            .id(cessationType.getId())
            .code(cessationType.getCode())
            .name(cessationType.getName())
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
}