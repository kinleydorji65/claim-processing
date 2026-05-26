package com.claim.claim_processing.integration.contribution.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.repository.contribution.MemberContributionSnapshotRepository;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.claim.mapper.MemberContributionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberContributionServiceImpl implements MemberContributionService {

        private final MemberContributionSnapshotRepository snapshotRepository;
        private final MemberContributionMapper contributionMapper;

        @Override
        public MemberContributionSummary getContributionSummary(String nppfNumber) {
                return snapshotRepository.findByNppfNumber(nppfNumber)
                                .map(contributionMapper::toSummaryFromEntity) // Now maps Entity to Summary directly
                                .orElseGet(() -> emptySummary(nppfNumber));
        }

        // private MemberContributionSummary emptySummary(String nppfNumber) {

        //         List<MemberContributionSummary.ComponentGroup> groups = List.of(

        //                         // ================= PF Member Contribution (pf_mc) =================
        //                         MemberContributionSummary.ComponentGroup.builder()
        //                                         .code("pf_mc")
        //                                         .name("PF Member Contribution")
        //                                         .principal(new BigDecimal("100000"))
        //                                         .interest(new BigDecimal("20000"))
        //                                         .totalBalance(new BigDecimal("120000"))
        //                                         .interestRate(new BigDecimal("0.08"))
        //                                         .lastInterestDate(LocalDate.of(2024, 12, 31))
        //                                         .lastUpdatedDate(LocalDate.now())
        //                                         .build(),

        //                         // ================= PF Member Interest (pf_imc) =================
        //                         MemberContributionSummary.ComponentGroup.builder()
        //                                         .code("pf_imc")
        //                                         .name("PF Member Interest")
        //                                         .principal(new BigDecimal("0"))
        //                                         .interest(new BigDecimal("20000"))
        //                                         .totalBalance(new BigDecimal("20000"))
        //                                         .interestRate(new BigDecimal("0.08"))
        //                                         .lastInterestDate(LocalDate.of(2024, 12, 31))
        //                                         .lastUpdatedDate(LocalDate.now())
        //                                         .build(),

        //                         // ================= PF Employer Contribution (pf_ec) =================
        //                         MemberContributionSummary.ComponentGroup.builder()
        //                                         .code("pf_ec")
        //                                         .name("PF Employer Contribution")
        //                                         .principal(new BigDecimal("150000"))
        //                                         .interest(new BigDecimal("30000"))
        //                                         .totalBalance(new BigDecimal("180000"))
        //                                         .interestRate(new BigDecimal("0.08"))
        //                                         .lastInterestDate(LocalDate.of(2024, 12, 31))
        //                                         .lastUpdatedDate(LocalDate.now())
        //                                         .build(),

        //                         // ================= PF Employer Interest (pf_iec) =================
        //                         MemberContributionSummary.ComponentGroup.builder()
        //                                         .code("pf_iec")
        //                                         .name("PF Employer Interest")
        //                                         .principal(new BigDecimal("0"))
        //                                         .interest(new BigDecimal("30000"))
        //                                         .totalBalance(new BigDecimal("30000"))
        //                                         .interestRate(new BigDecimal("0.08"))
        //                                         .lastInterestDate(LocalDate.of(2024, 12, 31))
        //                                         .lastUpdatedDate(LocalDate.now())
        //                                         .build());

        //         // ================= totals =================
        //         BigDecimal totalBalance = groups.stream()
        //                         .map(MemberContributionSummary.ComponentGroup::getTotalBalance)
        //                         .reduce(BigDecimal.ZERO, BigDecimal::add);

        //         return MemberContributionSummary.builder()
        //                         .nppfNumber(nppfNumber)
        //                         .schemeTypeId(1L)
        //                         .pfJoiningDate(LocalDate.of(2015, 1, 1))
        //                         .pensionJoiningDate(LocalDate.of(2015, 1, 1))
        //                         // service period
        //                         .totalContributionMonths(120)
        //                         .totalContributionYears(10)
        //                         .totalNonContributionMonths(5)
        //                         .contributionStartDate(LocalDate.of(2015, 1, 1))
        //                         .contributionEndDate(LocalDate.of(2025, 1, 1))
        //                         // components
        //                         .componentGroups(groups)
        //                         // total
        //                         .totalBalance(totalBalance)
        //                         .build();
        // }

        private MemberContributionSummary emptySummary(String nppfNumber) {

    List<MemberContributionSummary.ComponentGroup> groups = List.of(

            // ================= PF Member Contribution (pf_mc) =================
            MemberContributionSummary.ComponentGroup.builder()
                    .code("PF_MC")
                    .name("PF Member Contribution")
                    .principal(new BigDecimal("5000"))
                    .interest(new BigDecimal("200"))
                    .totalBalance(new BigDecimal("5200"))
                    .interestRate(new BigDecimal("0.08"))
                    .lastInterestDate(LocalDate.of(2024, 12, 31))
                    .lastUpdatedDate(LocalDate.now())
                    .build(),

            // ================= PF Member Interest (pf_imc) =================
            MemberContributionSummary.ComponentGroup.builder()
                    .code("PF_IMC")
                    .name("PF Member Interest")
                    .principal(new BigDecimal("5000"))
                    .interest(new BigDecimal("200"))
                    .totalBalance(new BigDecimal("5200"))
                    .interestRate(new BigDecimal("0.08"))
                    .lastInterestDate(LocalDate.of(2024, 12, 31))
                    .lastUpdatedDate(LocalDate.now())
                    .build(),

            // ================= PF Employer Contribution (pf_ec) =================
            MemberContributionSummary.ComponentGroup.builder()
                    .code("PF_EC")
                    .name("PF Employer Contribution")
                    .principal(new BigDecimal("5000"))
                    .interest(new BigDecimal("200"))
                    .totalBalance(new BigDecimal("5200"))
                    .interestRate(new BigDecimal("0.08"))
                    .lastInterestDate(LocalDate.of(2024, 12, 31))
                    .lastUpdatedDate(LocalDate.now())
                    .build(),

            // ================= PF Employer Interest (pf_iec) =================
            MemberContributionSummary.ComponentGroup.builder()
                    .code("PF_IEC")
                    .name("PF Employer Interest")
                    .principal(new BigDecimal("0"))
                    .interest(new BigDecimal("200"))
                    .totalBalance(new BigDecimal("200"))
                    .interestRate(new BigDecimal("0.08"))
                    .lastInterestDate(LocalDate.of(2024, 12, 31))
                    .lastUpdatedDate(LocalDate.now())
                    .build()
    );

    // ================= totals =================
    BigDecimal totalBalance = groups.stream()
            .map(MemberContributionSummary.ComponentGroup::getTotalBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return MemberContributionSummary.builder()
            .nppfNumber(nppfNumber)
            .schemeTypeId(1L)
            .pfJoiningDate(LocalDate.of(2024, 6, 1))      // Joined recently
            .pensionJoiningDate(LocalDate.of(2024, 6, 1))
            // service period - LESS THAN 12 MONTHS for lapsed/termination
            .totalContributionMonths(8)                    // 8 months only
            .totalContributionYears(0)                     // Less than 1 year
            .totalNonContributionMonths(6)                 // 6 months gap
            .contributionStartDate(LocalDate.of(2024, 6, 1))
            .contributionEndDate(LocalDate.of(2025, 2, 1)) // Contribution ended 3 months ago
            // components
            .componentGroups(groups)
            // total
            .totalBalance(totalBalance)
            .build();
}
}
