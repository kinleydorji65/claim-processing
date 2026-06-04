package com.claim.claim_processing.common.mapper.others;

import com.claim.claim_processing.common.DTO.request.others.StatusMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.others.StatusResponseDTO;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    StatusMaster toEntity(StatusMasterRequestDto dto);

    StatusResponseDTO toDto(StatusMaster entity);
}