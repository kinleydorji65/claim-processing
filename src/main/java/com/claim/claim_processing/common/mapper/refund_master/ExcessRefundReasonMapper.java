package com.claim.claim_processing.common.mapper.refund_master;

import com.claim.claim_processing.common.DTO.request.refundMaster.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.refundMaster.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.ExcessRefundReasonUpdateDto;
import com.claim.claim_processing.common.entities.refundMaster.ExcessRefundReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExcessRefundReasonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExcessRefundReasonMaster toEntity(ExcessRefundReasonRequestDto dto);

    ExcessRefundReasonResponseDto toResponseDto(ExcessRefundReasonMaster entity);

    List<ExcessRefundReasonResponseDto> toResponseDtoList(List<ExcessRefundReasonMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            ExcessRefundReasonUpdateDto dto,
            @MappingTarget ExcessRefundReasonMaster entity
    );
}