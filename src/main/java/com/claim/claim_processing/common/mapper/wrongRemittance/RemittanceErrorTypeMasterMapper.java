package com.claim.claim_processing.common.mapper.wrongRemittance;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceErrorTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceErrorTypeResponseDto;
import com.claim.claim_processing.common.entities.wrongRemittanceMaster.WrongRemittanceErrorTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RemittanceErrorTypeMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    RemittanceErrorTypeResponseDto toResponseDto(
            WrongRemittanceErrorTypeMaster entity
    );

    List<RemittanceErrorTypeResponseDto> toResponseDtoList(
            List<WrongRemittanceErrorTypeMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    WrongRemittanceErrorTypeMaster toEntity(
            RemittanceErrorTypeRequestDto dto
    );

    // =========================
    // PATCH UPDATE
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget WrongRemittanceErrorTypeMaster entity,
            RemittanceErrorTypeRequestDto dto
    );
}