package com.claim.claim_processing.application.mapper.detail;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiarySettlementDetailRequestDto;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BeneficiarySettlementDetailMapper {

    /**
     * REQUEST DTO -> ENTITY
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BeneficiarySettlementDetail toEntity(
            BeneficiarySettlementDetailRequestDto dto
    );

    /**
     * PARTIAL UPDATE
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void patchEntity(
            @MappingTarget BeneficiarySettlementDetail entity,
            BeneficiarySettlementDetailRequestDto dto
    );
}