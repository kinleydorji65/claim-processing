package com.claim.claim_processing.common.mapper.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedNoticeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedNoticeTypeResponseDto;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedNoticeTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnclaimedNoticeTypeMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    UnclaimedNoticeTypeResponseDto toResponseDto(UnclaimedNoticeTypeMaster entity);

    List<UnclaimedNoticeTypeResponseDto> toResponseDtoList(
            List<UnclaimedNoticeTypeMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UnclaimedNoticeTypeMaster toEntity(UnclaimedNoticeTypeRequestDto dto);

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
            @MappingTarget UnclaimedNoticeTypeMaster entity,
            UnclaimedNoticeTypeRequestDto dto
    );
}