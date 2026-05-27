package com.claim.claim_processing.application.mapper.detail;

import org.mapstruct.*;

import com.claim.claim_processing.application.DTO.request.detail.LegalRecoveryRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LegalRecoveryMapper {

    // ---------- Entity -> Response ----------
    @Mapping(target = "claimApplicationId", source = "claimApplication.id")

    @Mapping(target = "recoveryReasonId", source = "recoveryReason.id")
    @Mapping(target = "recoveryReasonName", source = "recoveryReason.name")

    @Mapping(target = "payeeTypeId", source = "payeeType.id")
    @Mapping(target = "payeeTypeName", source = "payeeType.name")

    @Mapping(target = "schemeTypeId", source = "schemeType.id")
    @Mapping(target = "schemeTypeName", source = "schemeType.name")

    @Mapping(target = "currentStatusId", source = "currentStatus.statusId")
    @Mapping(target = "currentStatusName", source = "currentStatus.statuseName")

    @Mapping(target = "loanTypeId", source = "loanType.id")
    @Mapping(target = "loanTypeName", source = "loanType.name")

    @Mapping(target = "loanStatusId", source = "loanStatus.id")
    @Mapping(target = "loanStatusName", source = "loanStatus.name")
    LegalRecoveryResponseDto toResponseDto(LegalRecoveryDetail entity);


    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "recoveryReason", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "currentStatus", ignore = true)
    @Mapping(target = "loanType", ignore = true)
    @Mapping(target = "loanStatus", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LegalRecoveryDetail toEntity(LegalRecoveryRequestDto dto);


    // ---------- Update Entity ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "recoveryReason", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "currentStatus", ignore = true)
    @Mapping(target = "loanType", ignore = true)
    @Mapping(target = "loanStatus", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            LegalRecoveryRequestDto dto,
            @MappingTarget LegalRecoveryDetail entity
    );
}