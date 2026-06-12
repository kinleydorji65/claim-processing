package com.claim.claim_processing.application.mapper.detail;

import org.mapstruct.*;

import com.claim.claim_processing.application.DTO.request.detail.LegalRecoveryDetailRequest;
import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LegalRecoveryMapper {

    // ---------- Entity -> Response ----------
    @Mapping(target = "claimApplicationId", source = "claimApplication.id")
    @Mapping(target = "claimDetailId", source = "claimDetail.id")
    @Mapping(target = "payeeTypeId", source = "payeeType.id")
    @Mapping(target = "payeeTypeName", source = "payeeType.name")
    @Mapping(target = "currentStatusId", source = "currentStatus.statusId")
    @Mapping(target = "currentStatusName", source = "currentStatus.statusName")
    LegalRecoveryResponseDto toResponseDto(LegalRecoveryDetail entity);


    // ---------- Request -> Entity ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "currentStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    LegalRecoveryDetail toEntity(LegalRecoveryDetailRequest dto);


    // ---------- Update Entity ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "currentStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            LegalRecoveryDetailRequest dto,
            @MappingTarget LegalRecoveryDetail entity
    );
}