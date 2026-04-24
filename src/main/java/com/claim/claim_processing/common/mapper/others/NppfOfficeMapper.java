package com.claim.claim_processing.common.mapper.others;

import com.claim.claim_processing.common.DTO.request.others.NppfOfficeRequestDto;
import com.claim.claim_processing.common.DTO.response.others.NppfOfficeResponseDto;
import com.claim.claim_processing.common.DTO.update.others.NppfOfficeUpdateDto;
import com.claim.claim_processing.common.entities.common.NppfOfficeMaster;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NppfOfficeMapper {

    // 🔹 Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NppfOfficeMaster toEntity(NppfOfficeRequestDto dto);

    // 🔹 Entity → Response
    NppfOfficeResponseDto toResponseDto(NppfOfficeMaster entity);

    List<NppfOfficeResponseDto> toResponseDtoList(List<NppfOfficeMaster> entities);

    // 🔹 Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(NppfOfficeUpdateDto dto,
                             @MappingTarget NppfOfficeMaster entity);
}