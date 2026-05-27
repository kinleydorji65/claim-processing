package com.claim.claim_processing.application.mapper.detail;

import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;

import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NormalClaimMapper {

    // ---------- Entity -> Response ----------
    @Mapping(target = "claimApplicationId", source = "claimApplication.id")

    @Mapping(target = "cessationTypeId", source = "cessationType.id")
    @Mapping(target = "cessationTypeName", source = "cessationType.name")

    @Mapping(target = "payeeTypeId", source = "payeeType.id")
    @Mapping(target = "payeeTypeName", source = "payeeType.name")

    @Mapping(target = "terminationReasonTypeId", source = "terminationReasonType.id")
    @Mapping(target = "terminationReasonTypeName", source = "terminationReasonType.name")
    NormalClaimResponseDto toResponseDto(NormalClaimDetail entity);


    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "terminationReasonType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
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