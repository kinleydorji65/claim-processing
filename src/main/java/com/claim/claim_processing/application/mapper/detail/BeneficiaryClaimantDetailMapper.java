package com.claim.claim_processing.application.mapper.detail;

import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BeneficiaryClaimantDetailMapper {

    @Mapping(target = "beneficiarySettlementDetailId", source = "beneficiarySettlementDetail.id")

    @Mapping(target = "nomineeId", source = "nominee.id")
    @Mapping(target = "nomineeFirstName", source = "nominee.firstName")
    @Mapping(target = "nomineeMiddleName", source = "nominee.middleName")
    @Mapping(target = "nomineeLastName", source = "nominee.lastName")

    @Mapping(target = "dependentId", source = "dependent.id")
    @Mapping(target = "dependentFirstName", source = "dependent.firstName")
    @Mapping(target = "dependentMiddleName", source = "dependent.middleName")
    @Mapping(target = "dependentLastName", source = "dependent.lastName")

    @Mapping(target = "claimantTypeId", source = "claimantType.id")
    @Mapping(target = "claimantTypeName", source = "claimantType.name")

    @Mapping(target = "payeeTypeId", source = "payeeType.id")
    @Mapping(target = "payeeTypeName", source = "payeeType.name")

    @Mapping(target = "relationshipTypeId", source = "relationshipType.relationTypeId")
    @Mapping(target = "relationshipTypeName", source = "relationshipType.relationTypeName")
    BeneficiaryClaimantResponseDto toDto(BeneficiaryClaimantDetail entity);

    List<BeneficiaryClaimantResponseDto> toDtoList(List<BeneficiaryClaimantDetail> entities);
}