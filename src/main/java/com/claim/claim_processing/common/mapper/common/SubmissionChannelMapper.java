package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.SubmissionChannelRequestDto;
import com.claim.claim_processing.common.DTO.response.common.SubmissionChannelResponseDto;
import com.claim.claim_processing.common.DTO.update.common.SubmissionChannelUpdateDto;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubmissionChannelMapper {

    // 🔹 Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true) // handled by entity default
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SubmissionChannelMaster toEntity(SubmissionChannelRequestDto dto);

    // 🔹 Entity → Response
    SubmissionChannelResponseDto toResponseDto(SubmissionChannelMaster entity);

    List<SubmissionChannelResponseDto> toResponseDtoList(List<SubmissionChannelMaster> entities);

    // 🔹 Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(SubmissionChannelUpdateDto dto,
                             @MappingTarget SubmissionChannelMaster entity);
}