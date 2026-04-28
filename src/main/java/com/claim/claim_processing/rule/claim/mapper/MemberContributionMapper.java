package com.claim.claim_processing.rule.claim.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.claim.claim_processing.common.DTO.response.contribution.MemberContributionDTO;
import com.claim.claim_processing.common.entities.contribution.MemberContributionSnapshot;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;

@Mapper(componentModel = "spring")
public interface MemberContributionMapper {

    MemberContributionMapper INSTANCE = Mappers.getMapper(MemberContributionMapper.class);

    // Map Entity to DTO
    MemberContributionDTO toDTO(MemberContributionSnapshot snapshot);
    
    // Map DTO to Summary
    @Mapping(source = "startDate", target = "contributionStartDate")
    @Mapping(source = "endDate", target = "contributionEndDate")
    MemberContributionSummary toSummary(MemberContributionDTO dto);
    
    // Combined mapping: Entity -> Summary
    default MemberContributionSummary toSummaryFromEntity(MemberContributionSnapshot snapshot) {
        return toSummary(toDTO(snapshot));
    }
}