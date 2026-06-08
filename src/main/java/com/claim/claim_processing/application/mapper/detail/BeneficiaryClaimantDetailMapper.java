package com.claim.claim_processing.application.mapper.detail;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiaryClaimantRequestDto;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BeneficiaryClaimantDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nominee", ignore = true)
    @Mapping(target = "dependent", ignore = true)
    @Mapping(target = "claimantType", ignore = true)
    @Mapping(target = "beneficiarySettlementDetail", ignore = true)
    @Mapping(target = "relationshipType", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BeneficiaryClaimantDetail toEntity(BeneficiaryClaimantRequestDto dto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nominee", ignore = true)
    @Mapping(target = "dependent", ignore = true)
    @Mapping(target = "claimantType", ignore = true)
    @Mapping(target = "beneficiarySettlementDetail", ignore = true)
    @Mapping(target = "relationshipType", ignore = true)
    @Mapping(target = "payeeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(
            BeneficiaryClaimantRequestDto dto,
            @MappingTarget BeneficiaryClaimantDetail entity
    );
}