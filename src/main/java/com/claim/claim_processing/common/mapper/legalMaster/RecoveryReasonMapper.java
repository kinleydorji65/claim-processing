package com.claim.claim_processing.common.mapper.legalMaster;

import com.claim.claim_processing.common.DTO.request.legalMaster.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.legalMaster.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legalMaster.RecoveryReasonUpdateDto;
import com.claim.claim_processing.common.entities.legalMaster.RecoveryReasonMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecoveryReasonMapper {

    // 🔹 Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RecoveryReasonMaster toEntity(RecoveryReasonRequestDto dto);

    // 🔹 Entity → Response
    RecoveryReasonResponseDto toResponseDto(RecoveryReasonMaster entity);

    List<RecoveryReasonResponseDto> toResponseDtoList(List<RecoveryReasonMaster> entities);

    // 🔹 Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(RecoveryReasonUpdateDto dto,
                             @MappingTarget RecoveryReasonMaster entity);
}