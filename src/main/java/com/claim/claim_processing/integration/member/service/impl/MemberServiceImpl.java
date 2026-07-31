package com.claim.claim_processing.integration.member.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.AgencyMemberDetail;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.others.MemberContributionJoiningDateHistory;
import com.claim.claim_processing.common.entities.others.PersonIdentity;
import com.claim.claim_processing.common.entities.others.member.MemberDetail;
import com.claim.claim_processing.common.repository.others.MemberContributionJoiningDateHistoryRepository;
import com.claim.claim_processing.common.repository.others.MemberDetailRepository;
import com.claim.claim_processing.common.repository.others.PersonIdentityRepository;
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
        private final MemberContributionJoiningDateHistoryRepository joiningDateHistoryRepository;
        private final PersonIdentityRepository personIdentityRepository;


        @Override
        public ApiResponseDTO<MemberDetailResponseDto> getMemberDetails(String nppfNumber) {

                MemberDetail memberDetail = memberDetailRepository
                                .findByNppfNumber(nppfNumber)
                                .orElseThrow(() -> new RuntimeException(
                                                "Member not found with NPPF number: " + nppfNumber));
                MemberDetailResponseDto responseDto = memberDetailMapper.toMemberDetailResponseDto(memberDetail);
                MemberContributionSummary contributionSummary = memberContributionService
                                .getContributionSummary(responseDto, null);

                
                MemberContributionJoiningDateHistory joiningDateDetail = joiningDateHistoryRepository.findByMemberCode(memberDetail.getMemberCode()).orElse(null);
                responseDto.setPfJoiningDate(joiningDateDetail != null ? joiningDateDetail.getCombinedPensionJoiningDate() : null);
                responseDto.setPensionJoiningDate(joiningDateDetail != null ? joiningDateDetail.getCombinedPensionJoiningDate() : null);

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

        @Override
        public ApiResponseDTO<List<AgencyMemberDetail>> getMemberDetailByAgencyCode(String agencyCode) {
                List<MemberDetail> memberDetails = memberDetailRepository.findByAgencyDetail_AgencyCode(agencyCode);
                if (memberDetails != null && memberDetails.isEmpty()) {
                        return ApiResponseDTO.success("Member Details not found with the Agency Code: " + agencyCode, null);
                }

                List<AgencyMemberDetail> results = memberDetails
                        .stream()
                        .map(m -> {
                                PersonIdentity identity = null;
                                if (m.getIdentityTypeId() != null && m.getIdentityTypeId() > 0) {
                                        identity = personIdentityRepository.findById(m.getIdentityTypeId()).orElse(null);
                                }
                                
                                return AgencyMemberDetail
                                        .builder()
                                        .memberName(getFullName(m.getFirstName(), m.getMiddleName(), m.getLastName()))
                                        .nppfNumber(m.getNppfNumber())
                                        .email(m.getEmail() != null ? m.getEmail() : null)
                                        .contactNo(m.getContactNo() != null ?m.getContactNo().toString() : null)
                                        .identityNumber(m.getIdentityNumber())
                                        .identityTypeName(identity != null ? identity.getName() : null)
                                        .build();
                        })
                        .toList();
                return ApiResponseDTO.success(results);
        }

        private String getFullName(String firstName, String middleName, String lastName) {
        return joinNonNullTrimmed(firstName, middleName, lastName);
    }

    private String joinNonNullTrimmed(String... parts) {
    return Arrays.stream(parts)
        .filter(p -> p != null && !p.isBlank())
        .collect(Collectors.joining(" "));
}

        private BigDecimal nullSafe(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }
}
