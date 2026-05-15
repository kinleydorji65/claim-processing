package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialWithdrawalReasonUpdateDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalCauseMaster;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartialWithdrawalCauseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PartialWithdrawalCauseMaster toEntity(PartialWithdrawalCauseRequestDto dto);

    @Mapping(target = "reason", source = "reason")
    PartialWithdrawalCauseResponseDto toResponseDto(PartialWithdrawalCauseMaster entity);

    List<PartialWithdrawalCauseResponseDto> toResponseDtoList(
            List<PartialWithdrawalCauseMaster> entities
    );

    PartialWithdrawalReasonResponseDto toReasonResponseDto(
            PartialWithdrawalReasonMaster reason
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            PartialWithdrawalCauseRequestDto dto,
            @MappingTarget PartialWithdrawalCauseMaster entity
    );
}