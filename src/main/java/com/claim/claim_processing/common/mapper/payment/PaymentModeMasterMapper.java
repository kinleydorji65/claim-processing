package com.claim.claim_processing.common.mapper.payment;

import com.claim.claim_processing.common.DTO.request.payment.PaymentModeRequestDto;
import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentModeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentModeMasterMapper {

    // -------------------------
    // ENTITY -> RESPONSE DTO
    // -------------------------
    PaymentModeResponseDto toResponseDto(PaymentModeMaster entity);

    List<PaymentModeResponseDto> toResponseDtoList(List<PaymentModeMaster> entities);

    // -------------------------
    // REQUEST DTO -> ENTITY
    // (CREATE)
    // -------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentModeMaster toEntity(PaymentModeRequestDto dto);

    // -------------------------
    // UPDATE (FULL REPLACE / PUT)
    // -------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            PaymentModeRequestDto dto,
            @MappingTarget PaymentModeMaster entity
    );

    // -------------------------
    // PATCH SUPPORT (IMPORTANT)
    // -------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntityFromDto(
            PaymentModeRequestDto dto,
            @MappingTarget PaymentModeMaster entity
    );
}