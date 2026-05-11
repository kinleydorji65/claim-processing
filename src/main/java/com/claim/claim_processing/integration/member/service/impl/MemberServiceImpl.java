package com.claim.claim_processing.integration.member.service.impl;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.others.MemberDetailRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.mapper.MemberDetailMapper;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    
    private final MemberDetailRepository memberDetailRepository;
    private final MemberContributionService memberContributionService;
    private final MemberDetailMapper memberDetailMapper;

    public ApiResponseDTO<MemberDetailResponseDto> getMemberDetails(String memberCode, String agencyCode) {
        MemberContributionSummary contributionSummary = memberContributionService.getContributionSummary(memberCode);
        MemberDetail memberDetail = memberDetailRepository.findByMemberCodeAndAgencyCode(memberCode, agencyCode)
                .orElseThrow(() -> new RuntimeException("Member not found with code: " + memberCode + " and agency code: " + agencyCode));  
        // This is a placeholder implementation and should be replaced with actual logic to call the member service
        MemberDetailResponseDto responseDto = memberDetailMapper.toMemberDetailResponseDto(memberDetail);
        responseDto.setPfJoiningDate(contributionSummary.getPfJoiningDate());
        responseDto.setPensionJoiningDate(contributionSummary.getPensionJoiningDate());
        return ApiResponseDTO.success(responseDto);
    }
}
