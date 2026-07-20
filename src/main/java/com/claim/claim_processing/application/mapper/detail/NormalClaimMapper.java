package com.claim.claim_processing.application.mapper.detail;

import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;

import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NormalClaimMapper {

    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)  // ← ADD THIS - set separately
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "terminationReasonType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // Map all other fields automatically
    NormalClaimDetail toEntity(NormalClaimRequestDto dto);



    // ---------- Update Entity ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "terminationReasonType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)

    void updateEntityFromDto(
            NormalClaimRequestDto dto,
            @MappingTarget NormalClaimDetail entity
    );
}