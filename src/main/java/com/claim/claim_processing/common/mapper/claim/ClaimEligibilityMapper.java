package com.claim.claim_processing.common.mapper.claim;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimEligibilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ClaimEligibilityMaster toEntity(ClaimEligibilityCreateRequestDto dto);

    @Mapping(target = "claimCircumstanceId", source = "claimCircumstance.id")
    @Mapping(target = "claimCircumstanceName", source = "claimCircumstance.name")
    @Mapping(target = "cessationTypeId", source = "cessationType.id")
    @Mapping(target = "cessationTypeName", source = "cessationType.name")
    @Mapping(target = "schemeTypeId", source = "schemeType.id")
    @Mapping(target = "schemeTypeName", source = "schemeType.name")
    ClaimEligibilityResponseDto toResponseDto(ClaimEligibilityMaster entity);

    List<ClaimEligibilityResponseDto> toResponseDtoList(List<ClaimEligibilityMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleCode", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(ClaimEligibilityUpdateRequestDto dto,
                             @MappingTarget ClaimEligibilityMaster entity);
}