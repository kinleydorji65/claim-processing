package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.PostingStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostingStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    PostingStatusResponseDto toResponseDto(PostingStatusMaster entity);

    List<PostingStatusResponseDto> toResponseDtoList(List<PostingStatusMaster> entities);

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PostingStatusMaster toEntity(PostingStatusRequestDto dto);

    // =========================
    // Update Existing Entity (PATCH)
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget PostingStatusMaster entity,
            PostingStatusRequestDto dto
    );
}