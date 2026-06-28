package com.claim.claim_processing.integration.contribution.service.impl;

import com.claim.claim_processing.common.repository.contribution.MemberContributionSnapshotRepository;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.claim.mapper.MemberContributionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberContributionServiceImpl implements MemberContributionService {

        private final MemberContributionSnapshotRepository snapshotRepository;
        private final MemberContributionMapper contributionMapper;

        @Override
        public MemberContributionSummary getContributionSummary(String nppfNumber) {
                return snapshotRepository.findByNppfNumber(nppfNumber)
                                .map(contributionMapper::toSummaryFromEntity)
                                .orElseGet(() -> emptySummary(nppfNumber));
        }

        private MemberContributionSummary emptySummary(String nppfNumber) {

                List<MemberContributionSummary.ComponentGroup> groups = List.of(
                                // PF Components - Using hardcoded values directly
                                component("PF_MC", "PF Member Contribution", "500000", "0", "500000"),
                                component("PF_IMC", "PF Member Interest (IMC)", "0", "2000", "2000"),
                                component("PF_EC", "PF Employer Contribution", "1000000", "0", "1000000"),
                                component("PF_IEC", "PF Employer Interest (IEC)", "0", "15000", "15000"),

                                // PC Components - Using hardcoded values directly
                                component("P_MC", "PC Member Contribution", "550000", "0", "550000"),
                                component("P_IMC", "PC Member Interest (IMC)", "0", "15000", "15000"),
                                component("P_EC", "PC Employer Contribution", "10000000", "0", "10000000"),
                                component("P_IEC", "PC Employer Interest (IEC)", "0", "15000", "15000"));

                // Now these totals will be correct
                BigDecimal totalPrincipalAmount = new BigDecimal("500000")
                                .add(new BigDecimal("0"))
                                .add(new BigDecimal("1000000"))
                                .add(new BigDecimal("0"))
                                .add(new BigDecimal("550000"))
                                .add(new BigDecimal("0"))
                                .add(new BigDecimal("10000000"))
                                .add(new BigDecimal("0"));
                // = 12,050,000

                BigDecimal totalInterestAmount = new BigDecimal("0")
                                .add(new BigDecimal("2000"))
                                .add(new BigDecimal("0"))
                                .add(new BigDecimal("15000"))
                                .add(new BigDecimal("0"))
                                .add(new BigDecimal("15000"))
                                .add(new BigDecimal("0"))
                                .add(new BigDecimal("15000"));
                // = 47,000

                BigDecimal totalBalance = new BigDecimal("500000")
                                .add(new BigDecimal("2000"))
                                .add(new BigDecimal("1000000"))
                                .add(new BigDecimal("15000"))
                                .add(new BigDecimal("550000"))
                                .add(new BigDecimal("15000"))
                                .add(new BigDecimal("10000000"))
                                .add(new BigDecimal("15000"));
                        
                MemberContributionSummary memberContributionSummary = null;
                if ("P000000098".equals(nppfNumber)) {
                        memberContributionSummary = MemberContributionSummary.builder()
                                        .nppfNumber(nppfNumber)
                                        .schemeTypeId(1L)
                                        .pfJoiningDate(LocalDate.of(2024, 6, 1))
                                        .pensionJoiningDate(LocalDate.of(2024, 6, 1))
                                        .totalContributionMonths(280)
                                        .totalContributionYears(23)
                                        .totalNonContributionMonths(6)
                                        .contributionStartDate(LocalDate.of(2024, 6, 1))
                                        .contributionEndDate(LocalDate.of(2025, 2, 1))
                                        .totalPrincipalAmount(totalPrincipalAmount) // 1,500,000
                                        .totalInterestAmount(totalInterestAmount) // 34,000
                                        .totalBalance(totalBalance) // 1,534,000
                                        .componentGroups(groups)
                                        .build();
                } else if (("P000000099").equals(nppfNumber)) {
                        memberContributionSummary = MemberContributionSummary.builder()
                                        .nppfNumber(nppfNumber)
                                        .schemeTypeId(1L)
                                        .pfJoiningDate(LocalDate.of(2024, 6, 1))
                                        .pensionJoiningDate(LocalDate.of(2024, 6, 1))
                                        .totalContributionMonths(6)
                                        .totalContributionYears(1)
                                        .totalNonContributionMonths(0)
                                        .contributionStartDate(LocalDate.of(2024, 6, 1))
                                        .contributionEndDate(LocalDate.of(2025, 2, 1))
                                        .totalPrincipalAmount(totalPrincipalAmount) // 0
                                        .totalInterestAmount(totalInterestAmount) // 0
                                        .totalBalance(totalBalance) // 0
                                        .componentGroups(groups) // Empty list for no contributions
                                        .build();
                        // }else if(("P000000096").equals(nppfNumber)){
                        // memberContributionSummary = MemberContributionSummary.builder()
                        // .nppfNumber(nppfNumber)
                        // .schemeTypeId(1L)
                        // .pfJoiningDate(LocalDate.of(2024, 6, 1))
                        // .pensionJoiningDate(LocalDate.of(2024, 6, 1))
                        // .totalContributionMonths(6)
                        // .totalContributionYears(1)
                        // .totalNonContributionMonths(0)
                        // .contributionStartDate(LocalDate.of(2024, 6, 1))
                        // .contributionEndDate(LocalDate.of(2025, 2, 1))
                        // .totalPrincipalAmount(totalPrincipalAmount) // 0
                        // .totalInterestAmount(totalInterestAmount) // 0
                        // .totalBalance(totalBalance) // 0
                        // .componentGroups(groups) // Empty list for no contributions
                        // .build();
                } else {
                        memberContributionSummary = MemberContributionSummary.builder()
                                        .nppfNumber(nppfNumber)
                                        .schemeTypeId(1L)
                                        .pfJoiningDate(LocalDate.of(2024, 6, 1))
                                        .pensionJoiningDate(LocalDate.of(2024, 6, 1))
                                        .totalContributionMonths(6)
                                        .totalContributionYears(1)
                                        .totalNonContributionMonths(0)
                                        .contributionStartDate(LocalDate.of(2024, 6, 1))
                                        .contributionEndDate(LocalDate.of(2025, 2, 1))
                                        .totalPrincipalAmount(totalPrincipalAmount) // 0
                                        .totalInterestAmount(totalInterestAmount) // 0
                                        .totalBalance(totalBalance) // 0
                                        .componentGroups(groups) // Empty list for no contributions
                                        .build();
                }

                return memberContributionSummary;
        }

        private MemberContributionSummary.ComponentGroup component(
                        String code,
                        String name,
                        String principal,
                        String interest,
                        String totalAmount) {

                BigDecimal principalAmount = new BigDecimal(principal);
                BigDecimal interestAmount = new BigDecimal(interest);
                BigDecimal totalAmountDecimal = new BigDecimal(totalAmount);

                return MemberContributionSummary.ComponentGroup.builder()
                                .componentCode(code)
                                .componentName(name)
                                .principalAmount(principalAmount)
                                .interestAmount(interestAmount)
                                .totalAmount(totalAmountDecimal) // Use pre-calculated total
                                .build();
        }
}