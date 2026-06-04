package com.claim.claim_processing.application.mapper.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;

import org.mapstruct.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClaimApplicationMapper {

    // ---------- Entity -> Response ----------
    @Mapping(target = "claimTypeId", source = "claimType.id")
    @Mapping(target = "claimTypeCode", source = "claimType.code")
    @Mapping(target = "claimTypeName", source = "claimType.name")

    @Mapping(target = "claimSourceId", source = "claimSource.id")
    @Mapping(target = "claimSourceCode", source = "claimSource.code")
    @Mapping(target = "claimSourceName", source = "claimSource.name")

    @Mapping(target = "submissionChannelId", source = "submissionChannel.id")
    @Mapping(target = "submissionChannelCode", source = "submissionChannel.code")
    @Mapping(target = "submissionChannelName", source = "submissionChannel.name")

    @Mapping(target = "schemeTypeId", source = "schemeType.id")
    @Mapping(target = "schemeTypeCode", source = "schemeType.code")
    @Mapping(target = "schemeTypeName", source = "schemeType.name")

    @Mapping(target = "memberCategoryId", source = "memberCategory.categoryId")
    @Mapping(target = "memberCategoryCode", source = "memberCategory.agencyCategoryCode")
    @Mapping(target = "memberCategoryName", source = "memberCategory.categoryName")

    @Mapping(target = "parentClaimApplicationId", source = "parentClaimApplication.id")

    @Mapping(target = "specialCaseAuthorityId", source = "specialCaseAuthority.id")
    @Mapping(target = "specialCaseAuthorityName", source = "specialCaseAuthority.name")

    @Mapping(target = "currentStageId", source = "currentStage.id")
    @Mapping(target = "currentStageName", source = "currentStage.name")

    @Mapping(target = "statusId", source = "status.statusId")
    @Mapping(target = "statusName", source = "status.statusName")

    @Mapping(target = "actionId", source = "action.id")
    @Mapping(target = "actionName", source = "action.name")

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "timestampToLocalDateTime")

    // keep children ignored here unless child mappers are ready
    @Mapping(target = "bankDetails", ignore = true)
    @Mapping(target = "deductionDetails", ignore = true)
    @Mapping(target = "loanDetails", ignore = true)
    @Mapping(target = "calculationSummaries", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "verifications", ignore = true)
    @Mapping(target = "approvals", ignore = true)
    @Mapping(target = "workflows", ignore = true)
    ClaimApplicationResponseDto toResponseDto(ClaimApplication entity);


    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationNumber", ignore = true)

    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "claimSource", ignore = true)
    @Mapping(target = "submissionChannel", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "memberCategory", ignore = true)
    @Mapping(target = "parentClaimApplication", ignore = true)
    @Mapping(target = "specialCaseAuthority", ignore = true)
    @Mapping(target = "currentStage", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "action", ignore = true)

    @Mapping(target = "normalClaimDetail", ignore = true)
    @Mapping(target = "partialWithdrawalDetail", ignore = true)
    @Mapping(target = "beneficiarySettlementDetail", ignore = true)
    @Mapping(target = "excessRefundDetail", ignore = true)
    @Mapping(target = "legalRecoveryDetail", ignore = true)
    @Mapping(target = "wrongRemittanceDetail", ignore = true)

    @Mapping(target = "bankDetails", ignore = true)
    @Mapping(target = "deductionDetails", ignore = true)
    @Mapping(target = "calculationSummaries", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "verifications", ignore = true)
    @Mapping(target = "approvals", ignore = true)
    @Mapping(target = "workflows", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimApplication toEntity(ClaimApplicationRequestDto dto);


    // ---------- Patch / Update ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationNumber", ignore = true)

    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "claimSource", ignore = true)
    @Mapping(target = "submissionChannel", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "memberCategory", ignore = true)
    @Mapping(target = "parentClaimApplication", ignore = true)
    @Mapping(target = "specialCaseAuthority", ignore = true)
    @Mapping(target = "currentStage", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "action", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntityFromDto(
            ClaimApplicationRequestDto dto,
            @MappingTarget ClaimApplication entity
    );


    // ---------- Helpers ----------
    @Named("timestampToLocalDateTime")
    default LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    @Named("stringToLong")
    default Long stringToLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}