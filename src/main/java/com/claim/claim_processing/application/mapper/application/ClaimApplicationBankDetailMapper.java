package com.claim.claim_processing.application.mapper.application;

import org.mapstruct.*;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationBankDetailRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.BankType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClaimApplicationBankDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "claimantType", ignore = true)
    @Mapping(target = "bankType", ignore = true)
    @Mapping(target = "isDefaultBank", source = "isDefaultBank", qualifiedByName = "toActivityEnumDefaultN")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimApplicationBankDetail toEntity(
            ClaimApplicationBankDetailRequestDto request
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "claimantType", ignore = true)
    @Mapping(target = "bankType", ignore = true)
    @Mapping(target = "isDefaultBank", source = "isDefaultBank", qualifiedByName = "toActivityEnumDefaultN")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            @MappingTarget ClaimApplicationBankDetail entity,
            ClaimApplicationBankDetailRequestDto request,
            @Context ClaimantTypeMaster claimantType,
            @Context BankType bankType
    );

    @AfterMapping
    default void afterToEntity(
            @MappingTarget ClaimApplicationBankDetail entity,
            @Context ClaimApplication claimApplication,
            @Context ClaimantTypeMaster claimantType,
            @Context BankType bankType
    ) {
        entity.setClaimApplication(claimApplication);
        entity.setClaimantType(claimantType);
        entity.setBankType(bankType);
    }

    @AfterMapping
    default void afterUpdate(
            @MappingTarget ClaimApplicationBankDetail entity,
            @Context ClaimantTypeMaster claimantType,
            @Context BankType bankType
    ) {
        if (claimantType != null) {
            entity.setClaimantType(claimantType);
        }

        if (bankType != null) {
            entity.setBankType(bankType);
        }
    }

    @Named("toActivityEnumDefaultN")
    default ActivityEnum toActivityEnumDefaultN(String value) {
        return toActivityEnum(value, ActivityEnum.N);
    }

    @Named("toActivityEnumDefaultY")
    default ActivityEnum toActivityEnumDefaultY(String value) {
        return toActivityEnum(value, ActivityEnum.Y);
    }

    default ActivityEnum toActivityEnum(String value, ActivityEnum defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return "Y".equalsIgnoreCase(value) ? ActivityEnum.Y : ActivityEnum.N;
    }
}