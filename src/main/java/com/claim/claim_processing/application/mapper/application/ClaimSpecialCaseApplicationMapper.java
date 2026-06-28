package com.claim.claim_processing.application.mapper.application;

import org.mapstruct.*;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimSpecialCaseApplicationMapper {

    /**
     * Convert Entity to Response DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "claimApplicationId", ignore = true)
    @Mapping(target = "memberCode", source = "memberCode")
    @Mapping(target = "nppfNumber", source = "nppfNumber")
    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "agencyCategoryId", source = "agencyCategoryId")
    @Mapping(target = "agencyCode", source = "agencyCode")
    @Mapping(target = "caseType", source = "caseType")
    @Mapping(target = "caseReason", source = "caseReason")
    @Mapping(target = "requestedAmount", source = "requestedAmount")
    @Mapping(target = "approvedAmount", source = "approvedAmount")
    @Mapping(target = "currentBenefitType", source = "currentBenefitType")
    @Mapping(target = "requestedBenefitType", source = "requestedBenefitType")
    @Mapping(target = "forfeitedComponentCodes", source = "forfeitedComponentCodes")
    @Mapping(target = "requestDate", source = "requestDate")
    @Mapping(target = "requestedBy", source = "requestedBy")
    @Mapping(target = "approvedBy", source = "approvedBy")
    @Mapping(target = "approvedDate", source = "approvedDate")
    @Mapping(target = "approvalReference", source = "approvalReference")
    @Mapping(target = "rejectionReason", source = "rejectionReason")
    @Mapping(target = "processedBy", source = "processedBy")
    @Mapping(target = "processedDate", source = "processedDate")
    @Mapping(target = "reserveAccountId", source = "reserveAccountId")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ClaimSpecialCaseApplicationResponseDto toResponseDto(ClaimSpecialCaseApplication entity);

    /**
     * Convert Request DTO to Entity (CREATE)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestDate", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvedDate", ignore = true)
    @Mapping(target = "approvalReference", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "processedBy", ignore = true)
    @Mapping(target = "processedDate", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimSpecialCaseApplication toEntity(ClaimSpecialCaseApplicationRequestDto dto);

    /**
     * Update existing entity from Request DTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "memberCode", ignore = true)
    @Mapping(target = "nppfNumber", ignore = true)
    @Mapping(target = "identityNumber", ignore = true)
    @Mapping(target = "agencyCategoryId", ignore = true)
    @Mapping(target = "agencyCode", ignore = true)
    @Mapping(target = "caseType", ignore = true)
    @Mapping(target = "requestDate", ignore = true)
    @Mapping(target = "requestedBy", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvedDate", ignore = true)
    @Mapping(target = "approvalReference", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "processedBy", ignore = true)
    @Mapping(target = "processedDate", ignore = true)
    @Mapping(target = "reserveAccountId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ClaimSpecialCaseApplicationRequestDto dto, @MappingTarget ClaimSpecialCaseApplication entity);

    /**
     * Convert List of Entities to List of Response DTOs
     */
    List<ClaimSpecialCaseApplicationResponseDto> toResponseDtoList(List<ClaimSpecialCaseApplication> entities);
}
