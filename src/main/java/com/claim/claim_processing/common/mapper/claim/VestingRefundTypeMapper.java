package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.VestingRefundTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.VestingRefundType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VestingRefundTypeMapper {

    // ✅ Entity → Response DTO
    VestingRefundTypeResponseDto toDto(VestingRefundType entity);

    // ✅ Request DTO → Entity (for create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    VestingRefundType toEntity(VestingRefundTypeRequestDto dto);

    // ✅ Update existing entity from DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(VestingRefundTypeRequestDto dto, @MappingTarget VestingRefundType entity);
}