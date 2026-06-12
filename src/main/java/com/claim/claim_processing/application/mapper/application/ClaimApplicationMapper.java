package com.claim.claim_processing.application.mapper.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;

import org.mapstruct.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClaimApplicationMapper {

    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationNumber", ignore = true)
    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "submissionChannel", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "memberCategory", ignore = true)
    @Mapping(target = "specialCaseAuthority", ignore = true)
    @Mapping(target = "currentStage", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "action", ignore = true)

    @Mapping(target = "normalClaimDetail", ignore = true)
    @Mapping(target = "partialWithdrawalDetail", ignore = true)
    @Mapping(target = "beneficiarySettlementDetail", ignore = true)
    // @Mapping(target = "legalRecoveryDetail", ignore = true)

    @Mapping(target = "bankDetails", ignore = true)
    @Mapping(target = "deductionDetail", ignore = true)
    @Mapping(target = "calculationSummary", ignore = true)
    @Mapping(target = "verificationDetail", ignore = true)
    @Mapping(target = "approvalDetail", ignore = true)
    @Mapping(target = "workflows", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimApplication toEntity(ClaimApplicationRequestDto dto);


    // ---------- Patch / Update ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationNumber", ignore = true)

    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "submissionChannel", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "memberCategory", ignore = true)
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