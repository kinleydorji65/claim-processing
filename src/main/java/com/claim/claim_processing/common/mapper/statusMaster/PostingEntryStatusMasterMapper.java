package com.claim.claim_processing.common.mapper.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.PostingEntryStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.PostingEntryStatusResponseDto;
import com.claim.claim_processing.common.entities.statusMaster.PostingEntryStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostingEntryStatusMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    PostingEntryStatusResponseDto toResponseDto(PostingEntryStatusMaster entity);

    List<PostingEntryStatusResponseDto> toResponseDtoList(
            List<PostingEntryStatusMaster> entities
    );

    // =========================
    // Request DTO -> Entity
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PostingEntryStatusMaster toEntity(PostingEntryStatusRequestDto dto);

    // =========================
    // Update Existing Entity (PATCH style)
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget PostingEntryStatusMaster entity,
            PostingEntryStatusRequestDto dto
    );
}