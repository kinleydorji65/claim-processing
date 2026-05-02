package com.claim.claim_processing.common.mapper.payment;

import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;
import com.claim.claim_processing.common.DTO.request.payment.PaymentStatusRequestDto;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentStatusMasterMapper {

    // -------------------------
    // ENTITY → RESPONSE DTO
    // -------------------------
    PaymentStatusResponseDto toResponseDto(PaymentStatusMaster entity);

    List<PaymentStatusResponseDto> toResponseDtoList(List<PaymentStatusMaster> entities);

    // -------------------------
    // CREATE (DTO → ENTITY)
    // -------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentStatusMaster toEntity(PaymentStatusRequestDto dto);

    // -------------------------
    // FULL UPDATE (PUT)
    // -------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            PaymentStatusRequestDto dto,
            @MappingTarget PaymentStatusMaster entity
    );

    // -------------------------
    // PARTIAL UPDATE (PATCH)
    // -------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntityFromDto(
            PaymentStatusRequestDto dto,
            @MappingTarget PaymentStatusMaster entity
    );
}