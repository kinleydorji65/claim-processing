package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReserveAccountMapper {

    /**
     * Convert Entity to Response DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "memberCode", source = "memberCode")
    @Mapping(target = "nppfNumber", source = "nppfNumber")
    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "agencyCategoryId", source = "agencyCategoryId")
    @Mapping(target = "agencyCode", source = "agencyCode")
    @Mapping(target = "reserveType", source = "reserveType")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "forfeitedAmount", source = "forfeitedAmount")
    @Mapping(target = "componentCode", source = "componentCode")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "releaseDate", source = "releaseDate")
    @Mapping(target = "releasedBy", source = "releasedBy")
    @Mapping(target = "releaseReference", source = "releaseReference")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ReserveAccountResponseDto toResponseDto(ReserveAccount entity);

    /**
     * Convert Request DTO to Entity (CREATE)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "releasedBy", ignore = true)
    @Mapping(target = "releaseReference", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ReserveAccount toEntity(ReserveAccountRequestDto dto);

    /**
     * Update existing entity from Request DTO (only updateable fields)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberCode", ignore = true)
    @Mapping(target = "nppfNumber", ignore = true)
    @Mapping(target = "identityNumber", ignore = true)
    @Mapping(target = "agencyCategoryId", ignore = true)
    @Mapping(target = "agencyCode", ignore = true)
    @Mapping(target = "reserveType", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "forfeitedAmount", ignore = true)
    @Mapping(target = "componentCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "releasedBy", ignore = true)
    @Mapping(target = "releaseReference", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ReserveAccountRequestDto dto, @MappingTarget ReserveAccount entity);

    /**
     * Convert List of Entities to List of Response DTOs
     */
    List<ReserveAccountResponseDto> toResponseDtoList(List<ReserveAccount> entities);
}