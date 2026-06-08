package com.claim.claim_processing.application.mapper.detail;


import org.mapstruct.*;

import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PartialWithdrawalMapper {

    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "withdrawalReason", ignore = true)
    @Mapping(target = "businessType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PartialWithdrawalDetail toEntity(PartialWithdrawalRequestDto dto);


    // ---------- Update Entity ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "withdrawalReason", ignore = true)
    @Mapping(target = "unemploymentCauseMaster", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            PartialWithdrawalRequestDto dto,
            @MappingTarget PartialWithdrawalDetail entity
    );
}