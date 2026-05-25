package com.claim.claim_processing.integration.member.service.impl;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.others.MemberDetailRepository;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.mapper.MemberDetailMapper;
import com.claim.claim_processing.integration.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    
    private final MemberDetailRepository memberDetailRepository;
    private final MemberContributionService memberContributionService;
    private final MemberDetailMapper memberDetailMapper;

    public ApiResponseDTO<MemberDetailResponseDto> getMemberDetails(String nppfNumber) {
        MemberContributionSummary contributionSummary = memberContributionService.getContributionSummary(nppfNumber);
        MemberDetail memberDetail = memberDetailRepository.findByNppfNumber(nppfNumber)
                .orElseThrow(() -> new RuntimeException("Member not found with NPPF number: " + nppfNumber));  
        // This is a placeholder implementation and should be replaced with actual logic to call the member service
        MemberDetailResponseDto responseDto = memberDetailMapper.toMemberDetailResponseDto(memberDetail);
        responseDto.setPfJoiningDate(contributionSummary.getPfJoiningDate());
        responseDto.setPensionJoiningDate(contributionSummary.getPensionJoiningDate());
        return ApiResponseDTO.success(responseDto);
    }
}
