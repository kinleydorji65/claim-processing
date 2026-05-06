package com.claim.claim_processing.common.mapper.arrRule;

import com.claim.claim_processing.common.DTO.request.arrRule.CreditMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.arrRule.CreditMethodResponseDto;
import com.claim.claim_processing.common.entities.arrMaster.CreditMethodMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditMethodMasterMapper {

    // ================= ENTITY -> RESPONSE =================
    CreditMethodResponseDto toResponseDto(CreditMethodMaster entity);

    List<CreditMethodResponseDto> toResponseDtoList(List<CreditMethodMaster> entities);

    // ================= REQUEST -> ENTITY =================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CreditMethodMaster toEntity(CreditMethodRequestDto dto);

    // ================= UPDATE ENTITY =================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreditMethodRequestDto dto, @MappingTarget CreditMethodMaster entity);
}