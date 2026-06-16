package com.claim.claim_processing.common.mapper.beneficiary;

import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimantTypeMapper {

    
    ClaimantTypeResponseDto toResponseDto(ClaimantTypeMaster entity);

    List<ClaimantTypeResponseDto> toResponseDtoList(List<ClaimantTypeMaster> entities);
}