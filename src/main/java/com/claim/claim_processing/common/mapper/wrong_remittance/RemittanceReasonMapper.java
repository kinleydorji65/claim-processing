package com.claim.claim_processing.common.mapper.wrong_remittance;

import com.claim.claim_processing.common.DTO.request.wrong_remittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.wrong_remittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrong_remittance.RemittanceReasonUpdateDto;
import com.claim.claim_processing.common.entities.wrong_remittance_master.WrongRemittanceReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RemittanceReasonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    WrongRemittanceReasonMaster toEntity(RemittanceReasonRequestDto dto);

    RemittanceReasonResponseDto toResponseDto(WrongRemittanceReasonMaster entity);

    List<RemittanceReasonResponseDto> toResponseDtoList(List<WrongRemittanceReasonMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            RemittanceReasonUpdateDto dto,
            @MappingTarget WrongRemittanceReasonMaster entity
    );
}