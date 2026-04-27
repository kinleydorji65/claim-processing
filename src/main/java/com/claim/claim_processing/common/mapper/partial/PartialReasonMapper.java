package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialWithdrawalReasonUpdateDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    imports = {ActivityEnum.class})
public interface PartialReasonMapper {

    // ================= CREATE =================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PartialWithdrawalReasonMaster toEntity(PartialWithdrawalReasonRequestDto dto);

    // ================= UPDATE =================

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", expression = "java(dto.getIsActive() != null ? ActivityEnum.valueOf(dto.getIsActive()) : entity.getIsActive())")
    void updateEntityFromDto(PartialWithdrawalReasonUpdateDto dto,
                             @MappingTarget PartialWithdrawalReasonMaster entity);

    // ================= RESPONSE =================

    PartialWithdrawalReasonResponseDto toResponseDto(PartialWithdrawalReasonMaster entity);

    List<PartialWithdrawalReasonResponseDto> toResponseDtoList(List<PartialWithdrawalReasonMaster> entities);
}