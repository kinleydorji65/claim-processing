package com.claim.claim_processing.integration.member.service.impl;

import java.math.BigDecimal;
import java.util.Objects;

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

        @Override
        public ApiResponseDTO<MemberDetailResponseDto> getMemberDetails(String nppfNumber) {

                MemberContributionSummary contributionSummary = memberContributionService
                                .getContributionSummary(nppfNumber, null);

                MemberDetail memberDetail = memberDetailRepository
                                .findByNppfNumber(nppfNumber)
                                .orElseThrow(() -> new RuntimeException(
                                                "Member not found with NPPF number: " + nppfNumber));

                MemberDetailResponseDto responseDto = memberDetailMapper.toMemberDetailResponseDto(memberDetail);

                responseDto.setPfJoiningDate(contributionSummary.getPfJoiningDate());
                responseDto.setPensionJoiningDate(contributionSummary.getPensionJoiningDate());

                // =========================
                // TOTAL BALANCE CALCULATION
                // =========================

                BigDecimal totalBalanceAmount = BigDecimal.ZERO;
                BigDecimal totalBalanceWithoutInterestAmount = BigDecimal.ZERO;

                if (contributionSummary != null && contributionSummary.getComponentGroups() != null) {

                        // CORRECT WAY: Use TotalAmount directly (already includes principal + interest)
                        totalBalanceAmount = contributionSummary.getComponentGroups()
                                        .stream()
                                        .filter(Objects::nonNull)
                                        .map(component -> nullSafe(component.getTotalAmount())) // Use getTotalAmount()
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        // This is correct - sum only principal amounts
                        totalBalanceWithoutInterestAmount = contributionSummary.getComponentGroups()
                                        .stream()
                                        .filter(Objects::nonNull)
                                        .map(component -> nullSafe(component.getPrincipalAmount()))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }

                responseDto.setTotalBalanceAmount(totalBalanceAmount);
                responseDto.setTotalBalanceWithoutInterestAmount(totalBalanceWithoutInterestAmount);

                return ApiResponseDTO.success(responseDto);
        }

        private BigDecimal nullSafe(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }
}
