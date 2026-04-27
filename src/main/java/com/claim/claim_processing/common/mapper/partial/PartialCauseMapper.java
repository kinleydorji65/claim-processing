package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialCauseUpdateDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartialCauseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PartialWithdrawalCauseMaster toEntity(PartialCauseRequestDto dto);

    PartialWithdrawalCauseResponseDto toResponseDto(PartialWithdrawalCauseMaster entity);

    List<PartialWithdrawalCauseResponseDto> toResponseDtoList(List<PartialWithdrawalCauseMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(PartialCauseUpdateDto dto,
                             @MappingTarget PartialWithdrawalCauseMaster entity);
}