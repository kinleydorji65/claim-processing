package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialReasonUpdateDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartialReasonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "Y")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PartialWithdrawalReasonMaster toEntity(PartialReasonRequestDto dto);

    PartialReasonResponseDto toResponseDto(PartialWithdrawalReasonMaster entity);

    List<PartialReasonResponseDto> toResponseDtoList(List<PartialWithdrawalReasonMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(PartialReasonUpdateDto dto,
                             @MappingTarget PartialWithdrawalReasonMaster entity);
}