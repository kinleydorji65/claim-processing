package com.claim.claim_processing.common.mapper.refundMaster;

import com.claim.claim_processing.common.DTO.request.refundMaster.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.refundMaster.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.RefundScopeUpdateDto;
import com.claim.claim_processing.common.entities.refundMaster.RefundScopeMaster;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RefundScopeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RefundScopeMaster toEntity(RefundScopeRequestDto dto);

    RefundScopeResponseDto toResponseDto(RefundScopeMaster entity);

    List<RefundScopeResponseDto> toResponseDtoList(List<RefundScopeMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(RefundScopeUpdateDto dto,
                             @MappingTarget RefundScopeMaster entity);
}