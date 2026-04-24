package com.claim.claim_processing.common.mapper.wrong_remittance;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrongRemittance.RemittanceReasonUpdateDto;
import com.claim.claim_processing.common.entities.wrongRemittanceMaster.WrongRemittanceReasonMaster;
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