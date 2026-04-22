package com.claim.claim_processing.common.mapper.refund_master;

import com.claim.claim_processing.common.DTO.request.refund_master.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.refund_master.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refund_master.ExcessRefundReasonUpdateDto;
import com.claim.claim_processing.common.entities.refund_master.ExcessRefundReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExcessRefundReasonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExcessRefundReasonMaster toEntity(ExcessRefundReasonRequestDto dto);

    @Mapping(target = "isActive", expression = "java(entity.getIsActive() != null ? String.valueOf(entity.getIsActive()) : null)")
    ExcessRefundReasonResponseDto toResponseDto(ExcessRefundReasonMaster entity);

    List<ExcessRefundReasonResponseDto> toResponseDtoList(List<ExcessRefundReasonMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(
            target = "isActive",
            expression = "java(dto.getIsActive() != null && !dto.getIsActive().isBlank() ? dto.getIsActive().charAt(0) : entity.getIsActive())"
    )
    void updateEntityFromDto(ExcessRefundReasonUpdateDto dto,
                             @MappingTarget ExcessRefundReasonMaster entity);
}