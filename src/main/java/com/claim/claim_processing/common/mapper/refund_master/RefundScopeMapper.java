package com.claim.claim_processing.common.mapper.refund_master;

import com.claim.claim_processing.common.DTO.request.refund_master.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.refund_master.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refund_master.RefundScopeUpdateDto;
import com.claim.claim_processing.common.entities.refund_master.RefundScopeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RefundScopeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "Y")
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