package com.claim.claim_processing.application.mapper.detail;


import org.mapstruct.*;

import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PartialWithdrawalMapper {

    // ---------- Entity -> Response ----------
    @Mapping(target = "claimApplicationId", source = "claimApplication.id")
    @Mapping(target = "applicationNumber", source = "claimApplication.applicationNumber")

    @Mapping(target = "payeeTypeId", source = "payeeType.id")
    @Mapping(target = "payeeTypeName", source = "payeeType.name")

    @Mapping(target = "partialWithdrawalMasterId", source = "partialWithdrawalMaster.id")
    @Mapping(target = "partialWithdrawalMasterName", ignore = true)

    @Mapping(target = "withdrawalReasonId", source = "withdrawalReason.id")
    @Mapping(target = "withdrawalReasonName", source = "withdrawalReason.name")

    @Mapping(target = "withdrawalCauseId", source = "withdrawalCause.id")
    @Mapping(target = "withdrawalCauseName", source = "withdrawalCause.name")

    @Mapping(target = "disasterTypeId", source = "disasterType.id")
    @Mapping(target = "disasterTypeName", source = "disasterType.name")

    @Mapping(target = "businessTypeId", source = "businessType.id")
    @Mapping(target = "businessTypeName", source = "businessType.name")
    PartialWithdrawalResponseDto toResponseDto(PartialWithdrawalDetail entity);


    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "partialWithdrawalMaster", ignore = true)
    @Mapping(target = "withdrawalReason", ignore = true)
    @Mapping(target = "withdrawalCause", ignore = true)
    @Mapping(target = "disasterType", ignore = true)
    @Mapping(target = "businessType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PartialWithdrawalDetail toEntity(PartialWithdrawalRequestDto dto);


    // ---------- Update Entity ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "partialWithdrawalMaster", ignore = true)
    @Mapping(target = "withdrawalReason", ignore = true)
    @Mapping(target = "withdrawalCause", ignore = true)
    @Mapping(target = "disasterType", ignore = true)
    @Mapping(target = "businessType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            PartialWithdrawalRequestDto dto,
            @MappingTarget PartialWithdrawalDetail entity
    );
}