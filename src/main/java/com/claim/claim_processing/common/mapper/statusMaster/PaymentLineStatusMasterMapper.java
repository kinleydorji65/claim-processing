package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PaymentLineStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PaymentLineStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.PaymentLineStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentLineStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    PaymentLineStatusResponseDto toResponseDto(PaymentLineStatusMaster entity);

    List<PaymentLineStatusResponseDto> toResponseDtoList(
            List<PaymentLineStatusMaster> entities
    );

    // =========================
    // Create DTO -> Entity
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PaymentLineStatusMaster toEntity(PaymentLineStatusRequestDto dto);

    // =========================
    // Update Existing Entity
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget PaymentLineStatusMaster entity,
            PaymentLineStatusRequestDto dto
    );
}