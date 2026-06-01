package com.claim.claim_processing.application.mapper.detail;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemittanceDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemittanceResponseDto;
import com.claim.claim_processing.application.entity.detail.WrongRemittanceDetail;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WrongRemittanceDetailMapper {

    // ======================================================
    // ENTITY -> RESPONSE DTO
    // ======================================================

    @Mapping(target = "claimApplicationId", source = "claimApplication.id")

    @Mapping(target = "wrongRemittanceReasonId", source = "wrongRemittanceReason.id")
    @Mapping(target = "wrongRemittanceReasonName", source = "wrongRemittanceReason.name")

    @Mapping(target = "contributionTypeId", source = "contributionType.id")
    @Mapping(target = "contributionTypeName", source = "contributionType.name")

    @Mapping(target = "affectedAccountTypeId", source = "affectedAccountType.id")
    @Mapping(target = "affectedAccountTypeName", source = "affectedAccountType.name")

    @Mapping(target = "errorTypeId", source = "errorType.id")
    @Mapping(target = "errorTypeName", source = "errorType.name")

    @Mapping(target = "payeeTypeId", source = "payeeType.id")
    @Mapping(target = "payeeTypeName", source = "payeeType.name")

    WrongRemittanceResponseDto toResponseDto(WrongRemittanceDetail entity);

    // ======================================================
    // REQUEST DTO -> ENTITY
    // ======================================================

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)

    @Mapping(target = "wrongRemittanceReason", ignore = true)

    @Mapping(target = "contributionType", ignore = true)

    @Mapping(target = "affectedAccountType", ignore = true)

    @Mapping(target = "errorType", ignore = true)

    @Mapping(target = "payeeType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)

    @Mapping(target = "updatedAt", ignore = true)

    WrongRemittanceDetail toEntity(WrongRemittanceDetailRequestDto dto);

    // ======================================================
    // UPDATE ENTITY
    // ======================================================

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)

    @Mapping(target = "wrongRemittanceReason", ignore = true)

    @Mapping(target = "contributionType", ignore = true)

    @Mapping(target = "affectedAccountType", ignore = true)

    @Mapping(target = "errorType", ignore = true)

    @Mapping(target = "payeeType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)

    @Mapping(target = "updatedAt", ignore = true)

    void updateEntityFromDto(
            WrongRemittanceDetailRequestDto dto,
            @MappingTarget WrongRemittanceDetail entity
    );
}