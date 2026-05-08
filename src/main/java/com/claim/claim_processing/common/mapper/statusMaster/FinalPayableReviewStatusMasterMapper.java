package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.FinalPayableReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.FinalPayableReviewStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.FinalPayableReviewStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FinalPayableReviewStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    FinalPayableReviewStatusResponseDto toResponseDto(FinalPayableReviewStatusMaster entity);

    List<FinalPayableReviewStatusResponseDto> toResponseDtoList(
            List<FinalPayableReviewStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    FinalPayableReviewStatusMaster toEntity(FinalPayableReviewStatusRequestDto dto);

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
            @MappingTarget FinalPayableReviewStatusMaster entity,
            FinalPayableReviewStatusRequestDto dto
    );
}